# Hazelcast

The Hazelcast Feedhandler consists of 3 types of subscription to Hazelcast:

1. Maps with Portable class serialization
2. Maps (HazelcastJsonValue type map values only)
3. Reliable Topic


## Subscribing to Hazelcast Maps with Portable class serialization

### Properties

Below is an example snippet of the *local.properties* setup:

```
ami.relay.fh.active=hazelcast
ami.relay.fh.hazelcast.class=com.f1.AmiHazelcastFHMapPortable
ami.relay.fh.hazelcast.props.url=localhost:5701
ami.relay.fh.hazelcast.props.maps=hzMap1,hzMap2
ami.relay.fh.hazelcast.props.cluster=dev
#get existing values in Hazelcast map: if true, sets true for all - default false
ami.relay.fh.hazelcast.props.getexistingvalues=false

#get existing values in Hazelcast map: for individual maps
ami.relay.fh.hazelcast.props.hzMap1.getexistingvalues=true
ami.relay.fh.hazelcast.props.hzMap2.getexistingvalues=false
#comma-delimited list of factories and corresponding factory IDs
ami.relay.fh.hazelcast.props.portablefactoryclasses=com.f1.ami.relay.fh.hazelcast.portable.PortableFactory
ami.relay.fh.hazelcast.props.portablefactoryids=333
#table name in AMI (optional - map name will be used if not provided)
ami.relay.fh.hazelcast.props.hzMap1.tablename=hzTable1
#your fully-qualified Hazelcast Portable Serializer class here
ami.relay.fh.hazelcast.props.hzMap1.portableclass=com.f1.ami.relay.fh.hazelcast.portable.PortableSerializer1
#needs to match portable class id
ami.relay.fh.hazelcast.props.hzMap1.portableclassid=1
#needs to match portable factory id
ami.relay.fh.hazelcast.props.hzMap1.portablefactoryid=333

ami.relay.fh.hazelcast.props.hzMap2.tablename=hzTable2
ami.relay.fh.hazelcast.props.hzMap2.portableclass=com.f1.ami.relay.fh.hazelcast.portable.PortableSerializer2
ami.relay.fh.hazelcast.props.hzMap2.portableclassid=2
ami.relay.fh.hazelcast.props.hzMap2.portablefactoryid=333
```

#### Additional (Optional) Properties

```
#Hazelcast credentials - if any
ami.relay.fh.hazelcast.props.username=demo
ami.relay.fh.hazelcast.props.password=demo123

#SSL default=off
ami.relay.fh.hazelcast.props.sslenabled=true
```

### Writing a Hazelcast Portable Serializer class for AMI

Below is an example of a Hazelcast Portable class implementation for deserialization for AMI. The feedhandler will extract all non-static primitive data type variables declared in the class provided as its table columns: which are `sampleInt`, `sampleDouble` and `sampleString` in the case of the example below.

``` java
package com.f1.ami.relay.fh.hazelcast.portable;

import java.io.IOException;

import com.f1.ami.relay.fh.hazelcast.AmiHazelcastPortableIDSetter;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

public class SamplePortable implements Portable, AmiHazelcastPortableIDSetter {

	static int ID = 0;
	static int FACTORY_ID = 0;
	
	private Integer sampleInt;
	private Double sampleDouble;
	private String sampleString;
	
	SamplePortable() {
		sampleInt = null;
		sampleDouble = null;
		sampleString = null;
	}
	
	SamplePortable(Integer sampleInt, Double sampleDouble, String sampleString) {
		this.sampleInt = sampleInt;
		this.sampleDouble = sampleDouble;
		this.sampleString = sampleString;
	}
	
	@Override
	public void setPortableClassID(int id) {
		SamplePortable.ID = id;
	}

	@Override
	public void setPortableFactoryID(int id) {
		SamplePortable.FACTORY_ID = id;
	}

	@Override
	public int getFactoryId() {
		return FACTORY_ID;
	}

	@Override
	public int getClassId() {
		return ID;
	}

	@Override
	public void writePortable(PortableWriter writer) throws IOException {
		writer.writeInt("sampleInt", this.sampleInt);
		writer.writeDouble("sampleDouble", this.sampleDouble);
		writer.writeString("sampleString", this.sampleString);
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		this.sampleInt = reader.readInt("sampleInt");
		this.sampleDouble = reader.readDouble("sampleDouble");
		this.sampleString = reader.readString("sampleString");
	}
}
```

## Subscribing to Hazelcast Maps with HazelcastJsonValue value types

### Properties

```
ami.relay.fh.active=hazelcast
ami.relay.fh.hazelcast.class=com.f1.AmiHazelcastFHMap
ami.relay.fh.hazelcast.props.url=localhost:5701
ami.relay.fh.hazelcast.props.maps=hzMap1,hzMap2
ami.relay.fh.hazelcast.props.cluster=dev

#get existing values in Hazelcast map: if true, sets true for all - default false
ami.relay.fh.hazelcast.props.getexistingvalues=false

#get existing values in Hazelcast map: for individual maps
ami.relay.fh.hazelcast.props.hzMap1.getexistingvalues=true
ami.relay.fh.hazelcast.props.hzMap2.getexistingvalues=false

#table name in AMI (optional - map name will be used if not provided)
ami.relay.fh.hazelcast.props.hzMap1.tablename=hzTable1
ami.relay.fh.hazelcast.props.hzMap2.tablename=hzTable2

#column mapping for key/value pairs from Hazelcast to AMI e.g. col1=int,col2=double,... (OPTIONAL - feedhandler will automatically convert to string type if not provided)
ami.relay.fh.hazelcast.props.hzMap1.mapping=id=int,name=string
ami.relay.fh.hazelcast.props.hzMap2.mapping=price=double,quantity=long
```

#### Additional (Optional) Properties

```
#Hazelcast credentials - if any
ami.relay.fh.hazelcast.props.username=demo
ami.relay.fh.hazelcast.props.password=demo123

#SSL default=off
ami.relay.fh.hazelcast.props.sslenabled=true
```

## Subscribing to Hazelcast Reliable Topics (valid JSON values only)

### Properties

```
ami.relay.fh.active=hazelcast
ami.relay.fh.hazelcast.class=com.f1.AmiHazelcastFHTopic
ami.relay.fh.hazelcast.props.url=localhost:5701
ami.relay.fh.hazelcast.props.topics=hzTopic1,hzTopic2
ami.relay.fh.hazelcast.props.cluster=dev
#table name in AMI (optional - topic name will be used if not provided)
ami.relay.fh.hazelcast.props.hzTopic1.tablename=hzTable1
ami.relay.fh.hazelcast.props.hzTopic2.tablename=hzTable2
#column mapping for key/value pairs from topic JSON value to AMI e.g. col1=int,col2=double,... (OPTIONAL - feedhandler will automatically convert to string type if not provided)
ami.relay.fh.hazelcast.props.hzTopic1.mapping=id=int,name=string
ami.relay.fh.hazelcast.props.hzTopic2.mapping=price=double,quantity=long
```

#### Additional (Optional) Properties

```
#Hazelcast credentials - if any
ami.relay.fh.hazelcast.props.username=demo
ami.relay.fh.hazelcast.props.password=demo123
#SSL default=off
ami.relay.fh.hazelcast.props.sslenabled=true
```

