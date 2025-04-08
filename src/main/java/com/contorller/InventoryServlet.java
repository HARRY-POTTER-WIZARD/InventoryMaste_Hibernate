package com.contorller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.dao.ProductDao;
import com.entity.Products;
import com.google.gson.Gson;
import com.util.HibernateUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@WebServlet(urlPatterns = {"/produtcs/*"})
public class InventoryServlet extends HttpServlet {
private ProductDao productDao;
private Gson gson;
@Override
	public void init() throws ServletException {
		productDao =new ProductDao();
		gson= new Gson();
	}
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
       
    	resp.setContentType("application/json");
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
        System.out.println("GET request received for path: " + path);

        try {
            if (path.equals("/")) {
                System.out.println("Fetching all Products.");
                HashMap<String,Object> obj=new HashMap<>();
                
                obj.put("Products",productDao.getAllProductss());
                resp.getWriter().write(gson.toJson(obj));
            } else if (path.startsWith("/edit/")) {
                int id = Integer.parseInt(path.substring(6));
                System.out.println("Fetching Product with ID: " + id);
                Products products = productDao.getProducts(id);
                if (products != null) {
                    resp.getWriter().write(gson.toJson(products));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Employee not found\"}");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Invalid endpoint\"}");
            }
        } catch (Exception e) {
            System.out.println("Error in doGet: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String action = req.getParameter("action");
        System.out.println("POST request received with action: " + action);
        Map<String, String> response = new HashMap<>();

        try {
            if ("register".equals(action)) {
                Products product = new Products(
                    req.getParameter("Name"),
                    req.getParameter("category"),
                    Double.parseDouble(req.getParameter("price")),
                    Integer.parseInt( req.getParameter("quantity"))
                );
                productDao.saveProducts(product);
                response.put("message", "Product added successfully");
            } else if ("update".equals(action)) {
                Products products = productDao.getProducts(Integer.parseInt(req.getParameter("id")));
                if (products != null) {
                    products.setName(req.getParameter("Name"));
                    products.setCategory(req.getParameter("category"));
                    products.setPrice(Double.parseDouble(req.getParameter("price")));
                    products.setQuantity(Integer.parseInt( req.getParameter("quantity")));
                    productDao.updateProducts(products);
                    response.put("message", "product updated successfully");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.put("error", "Product not found");
                }
            } else if ("delete".equals(action)) {
                int id =Integer.parseInt(req.getParameter("id")) ;
                productDao.deleteProducts(id);
                response.put("message", "Product deleted successfully");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.put("error", "Invalid action");
            }
        } catch (Exception e) {
            System.out.println("Error in doPost: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.put("error", e.getMessage());
        }
        resp.getWriter().write(gson.toJson(response));
    }

    @Override
    public void destroy() {
        HibernateUtil.shutdown(); // Cleanly close the SessionFactory
        System.out.println("Servlet destroyed, SessionFactory closed");
    }
}