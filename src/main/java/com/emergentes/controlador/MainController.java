package com.emergentes.controlador;

import com.emergentes.modelo.Libro;
import com.emergentes.utiles.ConexionBD;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String op;
            op = (request.getParameter("op") != null) ? request.getParameter("op") : "list";
            ArrayList<Libro> lista = new ArrayList<Libro>();
            ConexionBD canal = new ConexionBD();
            Connection conn = canal.conectar();
            PreparedStatement ps;
            ResultSet rs;

            if (op.equals("list")) {

                //obtiene la lista de registro
                String sql = "select * from libros";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Libro lib = new Libro();
                    lib.setId(rs.getInt("id"));
                    lib.setIsbn(rs.getString("isbn"));
                    lib.setTitulo(rs.getString("titulo"));
                    lib.setCategoria(rs.getString("categoria"));
                    lista.add(lib);
                }
                request.setAttribute("lista", lista);
                request.getRequestDispatcher("index.jsp").forward(request, response);

            }

            if (op.equals("nuevo")) {
                //nuevo registro
                Libro li = new Libro();

                request.setAttribute("lib", li);
                request.getRequestDispatcher("editar.jsp").forward(request, response);
            }
            if (op.equals("editar")) {
                //editar registro
                int id = Integer.parseInt(request.getParameter("id"));
                String sql = "select * from libros where id = ? ";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                rs = ps.executeQuery();
                Libro li = new Libro();
                while (rs.next()) {
                    li.setId(rs.getInt(1));
                    li.setIsbn(rs.getString(2));
                    li.setTitulo(rs.getString(3));
                    li.setCategoria(rs.getString(4));
                }

                request.setAttribute("lib", li);
                request.getRequestDispatcher("editar.jsp").forward(request, response);

            }
            if (op.equals("eliminar")) {
                //eliminar registro
                int id = Integer.parseInt(request.getParameter("id"));

                String sql = "delete from libros where id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);

                ps.executeUpdate();
                response.sendRedirect("MainController");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            ArrayList<Libro> lista = new ArrayList<Libro>();
            int id = Integer.parseInt(request.getParameter("id"));
            String isbn = request.getParameter("isbn");
            String titulo = request.getParameter("titulo");
            String categoria = request.getParameter("categoria");

            Libro lib = new Libro();

            lib.setIsbn(isbn);
            lib.setId(id);
            lib.setTitulo(titulo);
            lib.setCategoria(categoria);

            ConexionBD canal = new ConexionBD();
            Connection conn = canal.conectar();

            PreparedStatement ps;

            if (id == 0) {
                //Nuevo registro
                String sql = "insert into libros(isbn, titulo, categoria) values (?,?,?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, lib.getIsbn());
                ps.setString(2, lib.getTitulo());
                ps.setString(3, lib.getCategoria());

                ps.executeUpdate();
                response.sendRedirect("MainController");
            } else {
                String sql = "update libros set isbn =?, titulo = ?, categoria = ? where id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, lib.getIsbn());
                ps.setString(2, lib.getTitulo());
                ps.setString(3, lib.getCategoria());
                ps.setInt(4, lib.getId());
                ps.executeUpdate();
                response.sendRedirect("MainController");
            }
        } catch (SQLException e) {
            System.out.println("Error en SQL " + e.getMessage());
        }

    }

}
