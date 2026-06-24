module com.mycompany.securityhub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.mail;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.collections4;

    opens com.mycompany.securityhub to javafx.fxml;
    exports com.mycompany.securityhub;
}
