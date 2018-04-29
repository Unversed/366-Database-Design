/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author liaml
 */
@Named(value = "card")
@Dependent
public class Card implements Serializable {

    long cc_num;
    String username;
    LocalDate expiration;
    short cvv;
    
    private UIInput cardUI;
    private DBConnect dbConnect = new DBConnect();

    public long getCc_num() {
        return cc_num;
    }

    public void setCc_num(long cc_num) {
        this.cc_num = cc_num;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public short getCvv() {
        return cvv;
    }

    public void setCvv(short cvv) {
        this.cvv = cvv;
    }

    public UIInput getCardUI() {
        return cardUI;
    }

    public void setCardUI(UIInput cardUI) {
        this.cardUI = cardUI;
    }

    
    public static LocalDate getDate(short year, byte month ) {
        return LocalDate.of(year, month, 0);
    }

    public void validateExpiration() {
        
        boolean expired = expiration.isBefore(LocalDate.now());
        if (expired == true) {
            System.out.println("This card has already expired");
        }
    }
    
    
    public String go() throws SQLException {      
        //Util.invalidateUserSession();
        return "success";
    }
}
