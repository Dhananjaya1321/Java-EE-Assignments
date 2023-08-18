package lk.ijse.jsp.servlet;


import com.google.gson.Gson;
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


@WebServlet(urlPatterns = "/pages/item")
public class ItemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("select * from Item");
            ResultSet rst = pstm.executeQuery();

            resp.addHeader("Content-Type", "application/json");

            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            while (rst.next()) {
                String code = rst.getString(1);
                String name = rst.getString(2);
                int qtyOnHand = rst.getInt(3);
                double unitPrice = rst.getDouble(4);
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("code", code);
                objectBuilder.add("name", name);
                objectBuilder.add("qtyOnHand", qtyOnHand);
                objectBuilder.add("unitPrice", unitPrice);
                arrayBuilder.add(objectBuilder.build());
            }
            arrayBuilder.add(
                    Json.createObjectBuilder()
                            .add("state", "Ok")
                            .add("message", "Successfully loaded..!")
                            .add("data", "[]")
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

        String code = req.getParameter("code");
        String itemName = req.getParameter("description");
        String qty = req.getParameter("qty");
        String unitPrice = req.getParameter("unitPrice");
        String option = req.getParameter("option");
        resp.addHeader("Content-Type", "application/json");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");
            switch (option) {
                case "add":
                    PreparedStatement pstm = connection.prepareStatement("insert into Item values(?,?,?,?)");
                    pstm.setObject(1, code);
                    pstm.setObject(2, itemName);
                    pstm.setObject(3, qty);
                    pstm.setObject(4, unitPrice);
                    if (pstm.executeUpdate() > 0) {
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("state", "Ok");
                        objectBuilder.add("message", "Successfully Added...!");
                        objectBuilder.add("data", "[]");
                        resp.getWriter().print(objectBuilder.build());
                    }
                    break;
                case "delete":
                    PreparedStatement pstm2 = connection.prepareStatement("delete from Item where code=?");
                    pstm2.setObject(1, code);
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
            // Read the JSON data from the request's input stream
            BufferedReader reader = req.getReader();
            StringBuilder requestData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestData.append(line);
            }

            // Parse JSON data into Java object using Gson
            Gson gson = new Gson();
            ItemDTO updatedItem = gson.fromJson(requestData.toString(), ItemDTO.class);

            // Now you can access the fields of the updatedItem object
            String code = updatedItem.getCode();
            String itemName = updatedItem.getName();
            String qty = updatedItem.getQty();
            String unitPrice = updatedItem.getPrice();


            PreparedStatement pstm3 = connection.prepareStatement("update Item set description=?,qtyOnHand=?,unitPrice=? where code=?");
            pstm3.setObject(1, itemName);
            pstm3.setObject(2, Integer.parseInt(qty));
            pstm3.setObject(3, Double.parseDouble(unitPrice));
            pstm3.setObject(4, code);
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
            System.out.println(e.getMessage());
        }
    }
}
