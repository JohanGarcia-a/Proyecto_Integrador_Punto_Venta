package modelogenerico;

import java.util.List;

//con <T extends Entidad> aseguramos que la variable que remplace a T tena los valores de Entidad osea id y una tabla
public interface BaseDAO<T extends Entidad> {

	T buscarPorID(int id);

	List<T> ObtenerTodo();

	boolean agregar(T entidad);

	boolean modificar(T entidad);

	boolean borrar(int id);

}
