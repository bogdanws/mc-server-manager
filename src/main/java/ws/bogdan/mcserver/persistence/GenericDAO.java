package ws.bogdan.mcserver.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @param <T> tipul entitatii
 * @param <ID> tipul cheii primare
 */
public abstract class GenericDAO<T, ID> implements Repository<T, ID> {
    protected final Connection connection;

    protected GenericDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public abstract T save(T entity);

    @Override
    public abstract Optional<T> findById(ID id);

    @Override
    public abstract List<T> findAll();

    @Override
    public abstract T update(T entity);

    @Override
    public abstract boolean delete(ID id);

    protected PreparedStatement prepare(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
}
