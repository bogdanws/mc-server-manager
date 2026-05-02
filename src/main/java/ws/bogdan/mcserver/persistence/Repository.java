package ws.bogdan.mcserver.persistence;

import java.util.List;
import java.util.Optional;

/**
 * @param <T> tipul entitatii
 * @param <ID> tipul cheii primare
 */
public interface Repository<T, ID> {

    // insereaza sau inlocuieste o entitate
    T save(T entity);

    // cauta dupa cheia primara
    Optional<T> findById(ID id);

    // returneaza toate entitatile
    List<T> findAll();

    // actualizeaza o entitate existenta
    T update(T entity);

    // sterge entitatea cu cheia data si returneaza true daca a existat
    boolean delete(ID id);
}
