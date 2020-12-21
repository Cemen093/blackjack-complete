package org.itstep.blackjack;

import lombok.extern.slf4j.Slf4j;
import org.itstep.blackjack.card.Card;
import org.itstep.blackjack.event.GameEventListener;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Game {
    public static final int TWENTY_ONE = 21;
    public static final int THRESHOLD = 15;

    private final Player player;
    private final Player dealer;
    private final Deck deck;
    private final List<GameEventListener> eventListeners;
    int bet;

    public Game() {
        deck = new Deck();
        dealer = new Player(0);
        player = new Player(1000);
        eventListeners = new ArrayList<>();
        bet = 0;
    }

    public void hit() {
        Card card = deck.getOne();
        player.takeCard(card);
        log.info("Player take a card {}", card);
        publishPlayerTakeCard(card);
        if (player.getPoints() > TWENTY_ONE) {
            dealer.getCards().get(0).setHide(false);
            publishGameOver(getWinner());
            log.info("Game over. Win {}", getWinner());
        }
    }

    public void stand() {
        dealer.getCards().get(0).setHide(false);
        publishStand();
        log.info("Stand");
        while (dealer.getPoints() < THRESHOLD) {
            Card card = deck.getOne();
            dealer.takeCard(card);
            publishDealerTakeCard(card);
            log.info("Dealer take a card {}", card);
        }

        if (getWinner().equals("Player")){//Player?!?!
            player.addCash(bet * 2);//bet * 2?!?!
            changeCash(player.getCash());
        }
        publishGameOver(getWinner());
        log.info("Game over. Win {}", getWinner());
    }

    public void setBet(int amount) throws NoMoneyEnough {
        bet = amount;
        player.setBet(amount);
        changeCash(player.getCash());
    }

    public String getWinner() {
        if (player.getPoints() <= TWENTY_ONE) {
            if (dealer.getPoints() > TWENTY_ONE) {
                return "Player";
            } else if (player.getPoints() > dealer.getPoints()) {
                return "Player";
            } else {
                return "Dealer";
            }
        }
        return "Dealer";
    }

    public void play() {
        deck.shuffle();
        player.clear();
        dealer.clear();
        publishStart();
        log.info("Play game");

        Card firstCard = deck.getOne();
        player.takeCard(firstCard);
        publishPlayerTakeCard(firstCard);
        log.info("Player take first card {}", firstCard);

        Card second = deck.getOne();
        player.takeCard(second);
        publishPlayerTakeCard(second);
        log.info("Player take second card {}", second);

        Card hiddenCard = deck.getOne();
        hiddenCard.setHide(true);
        dealer.takeCard(hiddenCard);
        publishDealerTakeCard(hiddenCard);
        log.info("Dealer take hidden card {}", hiddenCard);

        Card lastCard = deck.getOne();
        dealer.takeCard(lastCard);
        publishDealerTakeCard(lastCard);
        log.info("Dealer take second card {}", lastCard);

        //тут должна быть проверка на Блек Джек, но как оказалось механика игры с тузом 11/1 не реализована
        //и Блек Джека в Блек Джеке нет
    }

    public void addGameEventListener(GameEventListener eventHandler) {
        eventListeners.add(eventHandler);
    }

    private void publishStand() {
        eventListeners.forEach(GameEventListener::stand);
    }

    private void publishStart() {
        eventListeners.forEach(GameEventListener::gameStart);
    }

    private void publishGameOver(String winner) {
        eventListeners.forEach(l -> l.gameOver(winner, player.getPoints(), dealer.getPoints()));
    }

    private void publishPlayerTakeCard(Card card) {
        eventListeners.forEach(l -> l.playerGetCard(card, player.getPoints()));
    }

    private void publishDealerTakeCard(Card card) {
        eventListeners.forEach(l -> l.dealerGetCard(card, dealer.getPoints()));
    }

    private void playerSetBet(int amount) {
        //???
    }

    private void changeCash(int cash){
        eventListeners.forEach(l -> l.changeCash(cash));
    }
}
