package com.f1.povo.f1app.reqres;

import java.util.List;
import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.povo.f1app.inspect.F1AppInspectionEntity;

@VID("F1.FA.ISPR")
public interface F1AppInspectPartitionResponse extends F1AppResponse {

	@PID(10)
	public void setPartitionSize(long partitionSizeBytes);
	public long getPartitionSize();

	@PID(11)
	public Map<String, Long> getInstances();
	public void setInstances(Map<String, Long> instances);

	@PID(12)
	public void setInspectionEntities(List<F1AppInspectionEntity> inspectionEntities);
	public List<F1AppInspectionEntity> getInspectionEntities();
}
