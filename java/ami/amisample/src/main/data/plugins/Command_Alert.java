
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;
import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.client.AmiClient;
import java.util.*;

public class Command_Alert implements com.f1.ami.sample.SampleCommandPlugin{
	
	public String getCommandDef(){
		
		return "C|I='Raise Alert'|M='1-'|F='I,T,Type,O,Account'|W=\"T==\\\"Order-Single\\\"\"";
	}
	
	public void init(ContainerTools tools, PropertyController props){
	}
	
	public void onCommand(AmiClient client, String requestId, String cmd, String userName, String objectType, String objectId, Map<String, Object> params){
		List<Map<String,Object>> values=(List)params.get("V");
		for(Map<String,Object> e:values){
			String type=(String)e.get("T");
			String id=(String)e.get("I");
			client.startObjectMessage(type, id, 0);
			client.addMessageParamString("status", "Error");
			client.sendMessageAndFlush();
		}
		client.startResponseMessage(requestId, 0,null);
		client.sendMessageAndFlush();
	}
	
}

