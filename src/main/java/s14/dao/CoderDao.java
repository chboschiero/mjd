package s14.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoderDao implements Dao<Coder> {
    private static final String GET_BY_PK = "SELECT coder_id, first_name, last_name, hire_date, salary FROM coders WHERE coder_id = ?";
    private static final String GET_ALL = "SELECT coder_id, first_name, last_name, hire_date, salary FROM coders";
    private static final String INSERT = "INSERT INTO coders(coder_id, first_name, last_name, hire_date, salary) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE coders SET first_name = ?, last_name = ?, hire_date = ?, salary = ? "
            + "WHERE coder_id = ?";
    private static final String DELETE = "DELETE FROM coders WHERE coder_id = ?";

    @Override
    public Optional<Coder> get(long id) {

        try (Connection conn = Connector.getConnection(); //
                PreparedStatement ps = conn.prepareStatement(GET_BY_PK)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                LocalDate hireDate = rs.getDate(4).toLocalDate();
                return Optional
                        .of(new Coder(rs.getLong(1), rs.getString(2), rs.getString(3), hireDate, rs.getDouble(5)));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Coder> getAll() {
        Connection conn = Connector.getConnection();

        List<Coder> results = new ArrayList<>();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(GET_ALL);

            while (rs.next()) {
                LocalDate hireDate = rs.getDate(4).toLocalDate();
                Coder coder = new Coder(rs.getLong(1), rs.getString(2), rs.getString(3), hireDate, rs.getDouble(5));
                results.add(coder);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return results;
    }

    @Override
    public void save(Coder coder) {
        Connection conn = Connector.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
            ps.setLong(1, coder.getId());
            ps.setString(2, coder.getFirstName());
            ps.setString(3, coder.getLastName());
            ps.setDate(4, Date.valueOf(coder.getHireDate()));
            ps.setDouble(5, coder.getSalary());
            ps.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void update(Coder coder) {
        Connection conn = Connector.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, coder.getFirstName());
            ps.setString(2, coder.getLastName());
            ps.setDate(3, Date.valueOf(coder.getHireDate()));
            ps.setDouble(4, coder.getSalary());
            ps.setLong(5, coder.getId());
            int count = ps.executeUpdate();
            if (count != 1) {
                System.out.println("Warning! Updated " + count + " lines for " + coder);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void delete(long id) {
        Connection conn = Connector.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setLong(1, id);
            int count = ps.executeUpdate();
            if (count != 1) {
                System.out.println("Warning! Deleted " + count + " lines for " + id);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
