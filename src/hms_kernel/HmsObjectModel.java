package hms_kernel;

import hms_kernel.data.DataSource;
import legion.data.MySqlDataSource;
import legion.kernel.LegionObject;

public abstract class HmsObjectModel extends LegionObject{

	@Override
	protected MySqlDataSource getDataSource() {
		return DataSource.getDataSource();
	}

}
