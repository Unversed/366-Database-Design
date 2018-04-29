
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/*
 * Manages Emnployee instances
 *
 * Employee Functions
 * -------------------
 *
 * change password 
 * add/delete customer 
 * view room prices 
 * check in customer 
 * check out customer 
 * add charges to customer 
 * view reservation 
 * cancel reservation 
 * create reservation
 */
@Named(value = "employee")
@SessionScoped
@ManagedBean
public class Employee implements Serializable {

    @ManagedProperty(value = "#{login}")
    private Login login;

    private DBConnect dbConnect = new DBConnect();
    private String username;
    private String first;
    private String last;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
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

    //TODO: date range
    public int viewRoomPrice(int roomNumber) throws SQLException {
        //Room base price by project spec
        int roomPrice = 100;

        //SQL to determine if current_date has special price
        String priceSQL = "SELECT price FROM price WHERE room_num = ? "
                + "AND date = current_date;";

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement(priceSQL);
        ps.setInt(1, roomNumber);

        ResultSet result = ps.executeQuery();
        if (result != null) {
            roomPrice = result.getInt(1);
        }

        result.close();
        con.close();

        return roomPrice;
    }

    private boolean existsCustomerUsername(String username) throws SQLException {
        Connection con = dbConnect.getConnection();

        String userExistsSQL = "SELECT username "
                + "FROM customers "
                + "WHERE username = ?;";
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        PreparedStatement ps = con.prepareStatement(userExistsSQL);
        ps.setString(1, username);
        ResultSet result = ps.executeQuery();
        if (!result.next()) {
            FacesMessage errorMessage = new FacesMessage("Username does not exist");
            throw new ValidatorException(errorMessage);
        }

        result.close();
        con.commit();
        con.close();
        return true;
    }

    //Returns reservation_id for given input, -1 if username does not exist
    private int getRIDFromUsername(String username) throws SQLException {
        int reservation_id;

        existsCustomerUsername(username);
        String getRidSQL = "SELECT reservation_id "
                + "FROM reservations "
                + "WHERE username = ? "
                + "AND current_date >= start_date"
                + "AND current_date <= end_date;";

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement(getRidSQL);
        ps.setString(1, username);

        ResultSet result = ps.executeQuery();
        if (!result.next()) {
            System.out.println("No reservation were found for " + username
                    + " where date range contained " + new Date().toString());
            return -1;
        }

        reservation_id = result.getInt(1);

        result.close();
        con.close();
        return reservation_id;
    }

    public int checkInCustomer(String username) throws SQLException {

        existsCustomerUsername(username);
        int roomNumber;
        String SQL = "SELECT room_num FROM reservations WHERE username = ? "
                + "AND start_date >= current_date "
                + "AND end_date <= current_date";

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setString(1, username);

        ResultSet result = ps.executeQuery();
        if (!result.next()) {
            System.out.println("No reservations were found for " + username
                    + " on " + new Date().toString());
            return -1;

            //Upon error we could have it go to the create reservation page 
            //#failElegantly
        }
        roomNumber = result.getInt(1);

        result.close();
        con.close();

        return roomNumber;
    }

    public String addCharge(String username, String description, int charge) throws SQLException {

        existsCustomerUsername(username);
        int reservation_id = getRIDFromUsername(username);

        String SQL = "INSERT INTO transactions (transaction_id, reservation_id, descrition, amount) "
                + "VALUES (NEXTVAL(transactions_transactions_id_seq), ?, ?, ?);";

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        Statement statement = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement(SQL);
        preparedStatement.setInt(1, reservation_id);
        preparedStatement.setString(2, description);
        preparedStatement.setInt(3, charge);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
        return "main";
    }

    //When a guest checks out, the system should display 
    //their total bill.
    public int checkOutCustomer(String username) throws SQLException {

        existsCustomerUsername(username);
        int reservation_id = getRIDFromUsername(username);
        int transactionSum;

        //Given reservation_id, get bill total
        String getSumSQL = "SELECT SUM(amount) "
                + "FROM transactions "
                + "WHERE reservation_id = ?";

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement(getSumSQL);
        ps.setInt(1, reservation_id);

        ResultSet result = ps.executeQuery();
        if (!result.next()) {
            System.out.println("No reservation were found for " + username
                    + " where date range contained " + new Date().toString());
            return -1;
        }

        transactionSum = result.getInt(1);

        result.close();
        con.close();

        return transactionSum;
    }

    public String createEmployee() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        //Should user type be "Employee"?
        Util.createLogin(username, password, "Customer", con);
        Statement statement = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement("Insert into employees values(?,?,?)");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, first);
        preparedStatement.setString(3, last);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        return "main";
    }

    public String deleteEmployee() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        Util.deleteLogin(username, con);
        con.close();
        return "main";
    }

    public void employeeUsernameExists(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        if (!existsUsername((String) value)) {
            FacesMessage errorMessage = new FacesMessage("Username does not exist");
            throw new ValidatorException(errorMessage);
        }
    }

    public void validateUsername(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        String user = (String) value;
        if (user == null) {
            FacesMessage errorMessage = new FacesMessage("Username must be not null");
            throw new ValidatorException(errorMessage);
        }
        if (existsUsername(user)) {
            FacesMessage errorMessage = new FacesMessage("Username already exist");
            throw new ValidatorException(errorMessage);
        }
    }

    private boolean existsUsername(String user) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        PreparedStatement ps = con.prepareStatement("select username from employees where username = ?");
        ps.setString(1, user);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            result.close();
            con.close();
            return true;
        }
        result.close();
        con.close();
        return false;
    }

}
