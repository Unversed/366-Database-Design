
/**
 *
 * @author lukes
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.Date;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;

/*
 * Manages admin instances
 * 
 * Admin Functions
 * -------------------
 * change his/her password
 * view room prices
 * change room prices
 * add employee
 * delete employee
 * 
 */
@Named(value = "admin")
@SessionScoped
@ManagedBean
public class Admin implements Serializable {

    @ManagedProperty(value = "#{login}")
    private Login login;
    private DBConnect dbConnect = new DBConnect();
    //@ManagedProperty(value = "#{password}")
    private String password;

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String updatePassword() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        Statement statement = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement(
                "update login set password = '" + password
                + "' where username = '" + login.getLogin() + "'");
        preparedStatement.executeUpdate();
        statement.close();
        con.close();
        return "success";
    }
}
