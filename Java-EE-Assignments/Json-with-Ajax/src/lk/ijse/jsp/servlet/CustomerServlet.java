package lk.ijse.jsp.servlet;


import com.google.gson.Gson;
import lk.ijse.jsp.dto.CustomerDTO;
import lk.ijse.jsp.dto.ItemDTO;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(urlPatterns = {"/pages/customer"})
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("select * from Customer");
            ResultSet rst = pstm.executeQuery();
            resp.addHeader("Content-Type", "application/json");

            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            while (rst.next()) {
                String id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("id", id);
                objectBuilder.add("name", name);
                objectBuilder.add("address", address);
                arrayBuilder.add(objectBuilder.build());
            }
            arrayBuilder.add(
                    Json.createObjectBuilder()
                            .add("state","Ok")
                            .add("message","Successfully loaded..!")
                            .add("data","[]")
            );
            resp.getWriter().print(arrayBuilder.build());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("state", "Error");
            objectBuilder.add("message", e.getMessage());
            objectBuilder.add("data", "[]");
            resp.setStatus(400);
            resp.getWriter().print(objectBuilder.build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");
        String option = req.getParameter("option");

        resp.addHeader("Content-Type", "application/json");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            switch (option) {
                case "add":
                    PreparedStatement pstm = connection.prepareStatement("insert into Customer values(?,?,?)");
                    pstm.setObject(1, cusID);
                    pstm.setObject(2, cusName);
                    pstm.setObject(3, cusAddress);
                    if (pstm.executeUpdate() > 0) {
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("state", "Ok");
                        objectBuilder.add("message", "Successfully Added...!");
                        objectBuilder.add("data", "[]");
                        resp.getWriter().print(objectBuilder.build());
                    }
                    break;
                case "delete":
                    PreparedStatement pstm2 = connection.prepareStatement("delete from Customer where id=?");
                    pstm2.setObject(1, cusID);
                    if (pstm2.executeUpdate() > 0) {
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("state", "Ok");
                        objectBuilder.add("message", "Successfully Deleted...!");
                        objectBuilder.add("data", "[]");
                        resp.getWriter().print(objectBuilder.build());
                    }
                    break;
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            resp.addHeader("Content-Type", "application/json");
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("state", "Error");
            objectBuilder.add("message", e.getMessage());
            objectBuilder.add("data", "[]");
            resp.setStatus(400);
            resp.getWriter().print(objectBuilder.build());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            resp.setContentType("application/json");

            BufferedReader reader = req.getReader();
            StringBuilder requestData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestData.append(line);
            }

            Gson gson = new Gson();
            CustomerDTO updatedCustomer = gson.fromJson(requestData.toString(), CustomerDTO.class);


            String id = updatedCustomer.getId();
            String name = updatedCustomer.getName();
            String address = updatedCustomer.getAddress();


            PreparedStatement pstm3 = connection.prepareStatement("update Customer set name=?,address=? where id=?");
            pstm3.setObject(3, id);
            pstm3.setObject(1, name);
            pstm3.setObject(2, address);

            if (pstm3.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("state", "Ok");
                objectBuilder.add("message", "Successfully Updated...!");
                objectBuilder.add("data", "[]");
                resp.getWriter().print(objectBuilder.build());
            }
        }  catch (Exception e) {
            resp.addHeader("Content-Type", "application/json");
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("state", "Error");
            objectBuilder.add("message", e.getMessage());
            objectBuilder.add("data", "[]");
            resp.setStatus(400);
            resp.getWriter().print(objectBuilder.build());
        }
    }
}
