package modelogenerico;

public interface Entidad {

	// id de cliente,proveedor,empleado, etc
	int getid();

	void setid(int id);

	// genera tabla que ocupara cliente,proveedor, etc
	Object[] toTableRow();

}