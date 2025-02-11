package com.compass.e_commerce.service;

import com.compass.e_commerce.clients.ChargeServiceClient;
import com.compass.e_commerce.dto.buy.BuyDetailsDto;
import com.compass.e_commerce.dto.buy.BuyItemsDto;
import com.compass.e_commerce.model.*;
import com.compass.e_commerce.model.enums.PaymentMethod;
import com.compass.e_commerce.model.enums.Stage;
import com.compass.e_commerce.model.pk.OrderGamePK;
import com.compass.e_commerce.repository.OrderRepository;
import com.compass.e_commerce.repository.SaleRepository;
import com.compass.e_commerce.service.interfaces.PurchasingService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointerException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchasingServiceImpl implements PurchasingService<BuyItemsDto> {

    private final UserServiceImpl userServiceImpl;
    private final AuthenticationServiceImpl authenticationServiceImpl;
    private final StockServiceImpl stockServiceImpl;
    private final OrderServiceImpl orderServiceImpl;
    private final ChargeServiceClient chargeServiceClient;
    private final EmailServiceImpl emailServiceImpl;
    private final CartServiceImpl cartServiceImpl;
    private final GameServiceImpl gameServiceImpl;
    private final SaleServiceImpl saleServiceImpl;

    @Override
    @Transactional
    public void buy(BuyItemsDto buyItemsDto) {
        User user = userServiceImpl.getById(authenticationServiceImpl.getAuthenticatedUserId());
        Order order = new Order(user, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), Stage.ACTIVE, PaymentMethod.PIX);
        double price = 0.0;

        Map<Long, Game> gamesMap = gameServiceImpl.findAllById(
                buyItemsDto.purchaseItems().stream().map(BuyItemsDto.PurchaseItems::id).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Game::getId, game -> game));

        for (BuyItemsDto.PurchaseItems items : buyItemsDto.purchaseItems()) {
            Game game = gamesMap.get(items.id());

            if (game == null) {
                throw new EntityNotFoundException("Não existe game com id: " + items.id());
            }

            OrderGames orderGames = new OrderGames();
            orderGames.setId(new OrderGamePK(order, game));
            orderGames.setQuantity(items.quantity());

            order.getOrderGames().add(orderGames);
            game.getOrderGames().add(orderGames);

            price += game.getPrice() * items.quantity();

            if (cartServiceImpl.checkIfTheGameIsInTheCart(items.id())) {
                cartServiceImpl.removeGameFromCart(items.id());
            }
        }
        order.setTotalPrice(price);
        try {
            String charge = chargeServiceClient.pixCreateCharge(
                    new BuyDetailsDto(user.getLogin(), user.getCpf(), String.valueOf(price),
                    "Campo 1", "Vazio", "Campo 2", "Vazio")
            );
            String jsonValueTxId = returnDataOfJson(charge, "txid", null, null);
            order.setQrCodeId(jsonValueTxId);
            orderServiceImpl.create(order);
        } catch (FeignException | DataIntegrityViolationException | IllegalArgumentException | JpaSystemException e) {
            System.out.println("Ocorreu um error" + e);
        }
    }

    @Override
    @KafkaListener(topics = "payment-confirmation", groupId = "payment-confirmation-group")
    @Transactional
    public void purchaseConfirmation(String message) {
        String jsonValueTxId = returnDataOfJson(message, "txid", null, "pix");
        String detailsCharge = chargeServiceClient.pixDetailsCharge(jsonValueTxId);
        System.out.println("Pagamento confirmado: " + message);
        System.out.println("Deltahes cobranca" + detailsCharge);
        String jsonValueCpf = returnDataOfJson(detailsCharge, "cpf", "devedor", null);
        System.out.println(jsonValueCpf);

        Order order = null;

        User user = userServiceImpl.findByCpf(jsonValueCpf);

        for (Order orderOfUser : user.getOrders()) {
            if (orderOfUser.getQrCodeId().equals(jsonValueTxId)) order = orderOfUser;
        }

        if (order == null) {
            throw new EntityNotFoundException("Pedido não encontrado para o txid: " + jsonValueTxId);
        }

        Sale sale = new Sale(order, LocalDateTime.now(), Double.valueOf(returnDataOfJson(detailsCharge, "valor", null, "pix")));

        order.setStage(Stage.PAID);
        orderServiceImpl.create(order);
        saleServiceImpl.create(sale);

        order.getOrderGames().stream().forEach(orderGames -> stockServiceImpl.stockReduction(orderGames.getGame(), orderGames.getQuantity()));
        emailServiceImpl.sendEmail(user.getEmail(), "Confirmação de Pagamento", "Olá " + user.getLogin()
                + "\nRecebemos o Pagamento referente ao seu pedido" + "\nItems do pedido: " + order.getOrderGames() + "\nValor Total: " + order.getTotalPrice());
    }

    private String returnDataOfJson(String jsonString, String key, String parentKey, String jsonArray) {
        try {
            JSONObject json = new JSONObject(jsonString);
            if ((jsonArray == null || jsonArray.isEmpty()) && (parentKey == null || parentKey.isEmpty())) {
                return json.getString(key);
            } else if (parentKey == null || parentKey.isEmpty()) {
                JSONArray array = json.getJSONArray(jsonArray);
                return array.getJSONObject(0).getString(key);
            } else {
                return json.getJSONObject(parentKey).getString(key);
            }
        } catch (Exception e) {
            throw new JSONPointerException("Error ao analisar json: " + e.getMessage());
        }
    }
}





