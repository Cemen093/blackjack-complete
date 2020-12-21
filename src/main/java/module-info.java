module org.itstep {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.slf4j;
    requires static org.mapstruct.processor;
    exports org.itstep.ui.controller;
    exports org.itstep.ui;
    exports org.itstep.blackjack;
    exports org.itstep;
    exports org.itstep.blackjack.card;
    exports org.itstep.blackjack.event;
    opens org.itstep.ui.controller;
}