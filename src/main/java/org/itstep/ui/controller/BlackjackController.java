package org.itstep.ui.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import org.itstep.blackjack.Game;
import org.itstep.blackjack.NoMoneyEnough;
import org.itstep.blackjack.card.Card;
import org.itstep.blackjack.event.GameEventListener;
import org.itstep.ui.CardView;

import java.net.URL;
import java.util.ResourceBundle;

public class BlackjackController implements Initializable {

    @FXML
    private Label lblDealer;
    @FXML
    private Label lblBlackJack;
    @FXML
    private Label lblPlayer;
    @FXML
    private Label lblCash;

    @FXML
    private Button btnPlay;
    @FXML
    private Button btnStand;
    @FXML
    private Button btnHit;

    @FXML
    private TextField tfBet;

    @FXML
    private HBox hbDealerCards;
    @FXML
    private HBox hbPlayerCards;

    private final BooleanProperty start = new SimpleBooleanProperty(false);

    private final Game game;

    private void updatePlayerPoints(int points) {
        lblPlayer.setText("Player: " + points);
    }

    private void updateDealerPoints(int points) {
        lblDealer.setText("Dealer: " + points);
    }

    public BlackjackController() {
        game = new Game();
        game.addGameEventListener(new GameEventHandler());
    }

    @FXML
    public void onPlay(ActionEvent actionEvent) {
        restart();
        game.play();
        try {
            game.setBet(getBet());
        } catch (NoMoneyEnough noMoneyEnough) {
            noMoneyEnough();
            //странно было начинать игру не проверяя хватает ли у игрока денег,
            //но с BooleanProperty start я так и не разобрался
            //noMoneyEnough.printStackTrace();
        }
    }

    private int getBet() {
        if(tfBet.getText().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(tfBet.getText());
    }

    @FXML
    public void onStand(ActionEvent actionEvent) {
        game.stand();
    }

    @FXML
    void onHit(ActionEvent actionEvent) {
        game.hit();
    }

    private void restart() {
        hbPlayerCards.getChildren().clear();
        hbDealerCards.getChildren().clear();
        start.set(true);
    }

    private void noMoneyEnough(){
        hbPlayerCards.getChildren().clear();
        hbDealerCards.getChildren().clear();
        lblPlayer.setText("Player: 0");
        lblDealer.setText("Dealer: 0");
        start.set(false);
    }

    private void stop() {
        start.set(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfBet.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        }));

        btnHit.disableProperty().bind(start.not());
        btnStand.disableProperty().bind(start.not());
        lblBlackJack.visibleProperty().bind(start.not());
        btnPlay.disableProperty().bind(start);
        tfBet.disableProperty().bind(start);

        stop();
    }

    private class GameEventHandler implements GameEventListener {

        @Override
        public void gameStart() {
            restart();
        }

        @Override
        public void stand() {
            stop();
            CardView node = (CardView) hbDealerCards.getChildren().get(0);
            node.setHide(false);
        }

        @Override
        public void playerGetCard(Card card, int points) {
            hbPlayerCards.getChildren().add(new CardView(card));
            updatePlayerPoints(points);
        }

        @Override
        public void dealerGetCard(Card card, int points) {
            hbDealerCards.getChildren().add(new CardView(card));
            updateDealerPoints(points);
        }

        @Override
        public void playerSetBet(int amount) {
            //Это от сюда мы бет передаём а не на оборот, или я что-то не понял в плане игры?
            //BET блокируется и не может быть изменён во время игры, я даже не знаю где это вызвать.
        }

        @Override
        public void gameOver(String winner, int playerPoints, int dealerPoints) {
            stand();
            updatePlayerPoints(playerPoints);
            updateDealerPoints(dealerPoints);
            lblBlackJack.setText(winner + " WIN");
        }

        @Override
        public void changeCash(int cash) {
            lblCash.setText("Cash: " + cash);
        }
    }
}
