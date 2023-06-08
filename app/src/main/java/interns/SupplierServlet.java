package interns;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class SupplierServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final SupplierRepository repository;

    public SupplierServlet(SupplierRepository repository) {
        this.objectMapper = new ObjectMapper();
        this.repository = repository;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            if ("/supplier".equals(request.getPathInfo())) {
                switch (request.getMethod()) {
                    case "GET":
                        List<Supplier> suppliers = repository.getSuppliers();
                        if (suppliers.isEmpty()) {
                            writeResponse(response, 204, "application/json", null);
                        } else {
                            writeResponse(response, 200, "application/json", objectMapper.writeValueAsString(suppliers));
                        }
                        break;
                    case "POST":
                        Supplier supplier = objectMapper.readValue(request.getReader(), Supplier.class);
                        repository.createSupplier(supplier);
                        writeResponse(response, 201, "application/json", objectMapper.writeValueAsString(supplier));
                        break;
                    default:
                        break;
                }
            } else {
                writeResponse(response, 200, "text/plain", "Hello, World!");
                //todo build 404 page?
            }
        } catch (SQLException exception) {
            writeResponse(response, 500, "text/plain", exception.getLocalizedMessage());
        }
    }

    private static void writeResponse(HttpServletResponse response, int status, String contentType, String payload) throws IOException {
        response.setStatus(status);
        response.setContentType(contentType);
        response.setCharacterEncoding("UTF-8");
        if (payload != null) {
            PrintWriter out = response.getWriter();
            out.print(payload);
            out.flush();
        }
    }
}
