
import java.io.Serializable;

import java.util.*;
import java.text.SimpleDateFormat;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

@Named(value = "util")
@SessionScoped
@ManagedBean
public class Util implements Serializable {

    public static void invalidateUserSession() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
    }

    public void validateDate(FacesContext context, UIComponent component, Object value)
            throws Exception {

        try {
            Date d = (Date) value;
        } catch (Exception e) {
            FacesMessage errorMessage = new FacesMessage("Input is not a valid date");
            throw new ValidatorException(errorMessage);
        }
    }

    public static void createLogin(String username, String password, String user_type, Connection con) throws SQLException {
        Statement statement = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement("Insert into login values(?,?,?)");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, user_type);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
    }

    public static void deleteLogin(String username, Connection con) throws SQLException {
        Statement statement = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement("DELETE from login where username = ?");
        preparedStatement.setString(1, username);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
    }

    public static void createCard(long cc_num, String username, LocalDate expiration,
            short cvv, Connection con) throws SQLException {
        String cardSQL = "INSERT INTO cards values(?,?,?,?);";

        Statement statement = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement(cardSQL);
        preparedStatement.setLong(1, cc_num);
        preparedStatement.setString(2, username);
        preparedStatement.setObject(3, expiration);
        preparedStatement.setShort(4, cvv);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
    }

    //Assumes each username is associated with one card
    public static void deleteCard(String username, Connection con) throws SQLException {
        String cardSQL = "DELETE FROM cards "
                + "WHERE username = ?;";

        Statement statement = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement(cardSQL);
        preparedStatement.setString(1, username);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
    }
}
