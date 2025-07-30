# Redis

## Supported Redis Commands

This document lists the accepted return types on AMI for every supported Redis command.

1. The format for each command is as follows:  
	Command_name parameters ...  
	- accepted_return_types  
	- Additional comments if necessary.  
1. Some return types depend on the inclusion of optional parameters.  
1. "numeric" in this document means if a Redis command’s output is a number, then you may use int/float/double/byte/short/long, bounded by their respective range defined by Java, in AMI web.  
1. Almost every command’s accepted return has String, but String is not iterable in AMI.  
1. String ("OK") or numeric (1/0) means whatever is in the parenthesis is the default response if the query executes without error.  
1. Square bracket \[\] indicates optional parameters. A pike \| means choose 1 among many.  
1. AMI redis adapter’s syntax is case **insensitive.**  

**APPEND** key value  
-numeric/string  
**AUTH** \[username\] password  
-string  
**BITCOUNT** key \[ start end \[ BYTE \| BIT\]\]  
-numeric/string  
**BITOP** operation destkey key \[key ...\]  
-string/numeric  
-Operations are AND OR XOR NOT  
**BITPOS** key bit \[ start \[ end \[ BYTE \| BIT\]\]\]  
numeric/string  
**BLMOVE** source destination LEFT \| RIGHT LEFT \| RIGHT timeout  
string  
**BLPOP** key \[key ...\] timeout  
string/list  
**BRPOP** key \[key ...\] timeout  
string/list  
**BZPOPMAX** key \[key ...\] timeout  
list/string  

**BZPOPMIN** key \[key ...\] timeout  
list/string  
**COPY** source destination \[DB destination-db\] \[REPLACE\]  
string (true/false)  
**DBSIZE**  
numeric/string  
**DECR** key  
numeric/string  
**DECRBY** key decrement  
numeric/string  
**DEL** key \[key ...\]  
string/numeric (1/0)  
**DUMP** key  
string/binary (logging will not display content, it will say "x bytes")  
**ECHO** message  
string  
**EVAL** script numkeys \[key \[key ...\]\] \[arg \[arg ...\]\]  
depends on script  
**EXISTS** key \[key ...\]  
numeric/string  
**EXPIRE** key seconds \[ NX \| XX \| GT \| LT\]  
numeric/string  
**EXPIREAT** key unix-time-seconds \[ NX \| XX \| GT \| LT\]  
numeric/string  
**EXPIRETIME** key  
numeric/string  
**FLUSHALL**  
string ("OK")  
**FLUSHDB** \[ ASYNC \| SYNC\]  
string ("OK")  
**GEOADD** key \[ NX \| XX\] \[CH\] longitude latitude member \[ longitude latitude member ...\]  
numeric/string  
**GEODIST** key member1 member2 \[ M \| KM \| FT \| MI\]  
numeric/string  
**GEOHASH** key member \[member ...\]  
string/list  
**GEOPOS** key member \[member ...\]  
list/string  
**GEOSEARCH** key FROMMEMBER member \| FROMLONLAT longitude latitude BYRADIUS radius M \| KM \| FT \| MI \| BYBOX width height M \| KM \| FT \| MI \[ ASC \| DESC\] \[ COUNT count \[ANY\]\] \[WITHCOORD\] \[WITHDIST\] \[WITHHASH\]  
list/string  
**GEOSEARCHSTORE** destination source FROMMEMBER member \| FROMLONLAT longitude latitude BYRADIUS radius M \| KM \| FT \| MI \| BYBOX width height M \| KM \| FT \| MI \[ ASC \| DESC\] \[ COUNT count \[ANY\]\] \[STOREDIST\]  
numeric/string  
**GET** key  
numeric if it is a number, else string  
**GETDEL** key  
numeric (only if value consists of digits)/string  
**GETEX** key \[ EX seconds \| PX milliseconds \| EXAT unix-time-seconds \| PXAT unix-time-milliseconds \| PERSIST\]  
  
string/int/null  
**GETRANGE** key start end  
numeric if it is only digits, else string  
**HDEL** key field \[field ...\]  
numeric/string  
**HEXISTS** key field  
string ("true"/"false")  
**HGET** key field  
numeric (only if value consists of digits)/string  
**HGETALL** key  
string/map  
**HINCRBY** key field increment  
string/numeric  
**HINCRBYFLOAT** key field increment  
numeric/string  
**HKEYS** key  
set/string  
**HLEN** key  
numeric/string  
**HMGET** key field \[field ...\]  
string/list  
**HMSET** key field value \[ field value ...\]  
string ("OK")  
**HRANDFIELD** key \[ count \[WITHVALUES\]\]  
With COUNT, list/string  
Without COUNT, string  
**HSCAN** key cursor \[MATCH pattern\] \[COUNT count\]  
list/string  
**HSET** key field value \[ field value ...\]  
numeric/string  
**HSETNX** key field value  
numeric/string  
**HSTRLEN** key field  
numeric/string  
**HVALS** key  
string/list  
**INCR** key  
numeric/string  
**INCRBY** key increment  
numeric/string  
**INCRBYFLOAT** key increment  
numeric/string  
**INFO** \[section \[section ...\]\]  
string  
**KEYS** pattern  
set/string  
**LASTSAVE**  
numeric/string  
**LCS** key1 key2 \[LEN\] \[IDX\] \[MINMATCHLEN len\] \[WITHMATCHLEN\]  
With no options, e.g. lcs a b, returns string.  
With LEN, returns numeric/string.  
With IDX and WITHMATCHLEN or just IDX, returns list/string  
**LINDEX** key index  
numeric/string if content is a number, string otherwise.  
**LINSERT** key BEFORE \| AFTER pivot element - string/intLLEN key  
string/int  
**LMOVE** source destination LEFT \| RIGHT LEFT \| RIGHT  
string if element is string, numeric if element is a number.  
**LMPOP** numkeys key \[key ...\] LEFT \| RIGHT \[COUNT count\]  
list/string  
**LPOP** key \[count\]  
Without COUNT, string by default, numeric if element is a number. With COUNT, list/string.  
**LPOS** key element \[RANK rank\] \[COUNT num-matches\] \[MAXLEN len\] -  
With COUNT, list/string  
Without COUNT, numeric/string if element is a number, else string.  
**LPUSH** key element \[element ...\]  
numeric/string  
**LPUSHX** key element \[element ...\]  
numeric/string  
**LRANGE** key start stop  
list/string  
**LREM** key count element  
numeric/string  
**LSET** key index element  
string ("OK")  
**LTRIM** key start stop  
string ("OK")  
**MGET** key \[key ...\]  
list/string  
**MSETNX** key value \[ key value ...\]  
numeric/string  
**PERSIST** key  
numeric/string  
**PEXPIRE** key milliseconds \[ NX \| XX \| GT \| LT\]  
numeric/string  
**PEXPIREAT** key unix-time-milliseconds \[ NX \| XX \| GT \| LT\]  
numeric/string  
**PEXPIRETIME** key  
numeric/string  
**PING** \[message\]  
string/numeric if \[message\] consists of digits only, otherwise string.  
**RANDOMKEY**  
string, numeric/string if key consists of digits only.  
**RENAME** key newkey  
string ("OK")  
**RENAMENX** key newkey  
numeric (1/0)  
**RESTORE** key ttl serialized-value \[REPLACE\] \[ABSTTL\] \[IDLETIME seconds\] \[FREQ frequency\]  
- string ("OK")  
- Serialized value works differently in AMI because we do not have a base 32 binary string. Example usage below:  
- execute set a 1;  
- Binary b = execute dump a;  
- string s = execute RESTORE a 16597119999999 "\${binaryToStr16(b)}" replace absttl;  
- string res = execute get a;  
- session.log(res);  
**RPOP** key \[count\]  
Without COUNT, numeric/string if the element is a number, otherwise string.  
With COUNT, list/string.  
**RPUSH** key element \[element ...\]  
- numeric/string  
**RPUSHX** key element \[element ...\]  
- numeric/string  
**SADD** key member \[member ...\]  
- numeric  
**SAVE**  
- string ("OK")  
**SCAN** cursor \[MATCH pattern\] \[COUNT count\] \[TYPE type\]  
- list/string  
**SCARD** key  
- numeric/string  
**SDIFF** key \[key ...\]  
- string/list  
**SDIFFSTORE** destination key \[key ...\]  
- numeric/string  
**SET** key value \[ NX \| XX \] \[GET\] \[ EX seconds \| PX milliseconds \| EXAT unix-time-seconds \| PXAT unix-time-milliseconds \| KEEPTTL \]  
- string ("OK")  
**SETBIT** key offset value  
- numeric/string 1/0  
**SETEX** key seconds value  
- string ("OK")  
**SETNX** key value  
- numeric (0/1)  
**SETRANGE** key offset value  
- numeric/string  
**SINTER** key \[key ...\]  
- set/string  
**SINTERCARD** numkeys key \[key ...\] \[LIMIT limit\]  
- numeric/string  
**SINTERSTORE** destination key \[key ...\]  
- numeric/string  
**SISMEMBER** key member  
- string (true/false)  
**SMEMBERS** key  
- set/string  
**SMISMEMBER** key member \[member ...\]  
- list/string  
**SMOVE** source destination member  
numeric  
**SORT** key \[BY pattern\] \[LIMIT offset count\] \[GET pattern \[GET pattern ...\]\] \[ ASC \| DESC\] \[ALPHA\] \[STORE destination\]  
Without STORE, list/string.  
With STORE, numeric  
**SORT_RO** key \[BY pattern\] \[LIMIT offset count\] \[GET pattern \[GET pattern ...\]\] \[ ASC \| DESC\] \[ALPHA\]  
list/string  
**SPOP** key \[count\]  
With COUNT, set/string.  
Without COUNT, numeric/string if the element is a number, otherwise string.  
**SRANDMEMBER** key \[count\]  
list/string  
**SREM** key member \[member ...\]  
numeric/string  
**SSCAN** key cursor \[MATCH pattern\] \[COUNT count\]  
list/string  
**STRLEN** key  
numeric/string  
**SUBSTR** key start end  
string  
**SUNION** key \[key ...\]  
set/string  
**SUNIONSTORE** destination key \[key ...\]  
numeric/string  
**TIME**  
list/string  
**TOUCH** key \[key ...\]  
numeric/string  
**TTL** key  
numeric/string  
**TYPE** key  
string  
**UNLINK** key  
numeric/string  
**ZADD** key \[ NX \| XX\] \[ GT \| LT\] \[CH\] \[INCR\] score member \[ score member ...\]  
numeric/string  
**ZCARD** key  
numeric/string  
**ZCOUNT** key min max  
numeric/string  
**ZDIFF** numkeys key \[key ...\] \[WITHSCORES\]  
set/string  
**ZDIFFSTORE** destination numkeys key \[key ...\]  
numeric/string  
**ZINCRBY** key increment member  
numeric/string  
**ZINTER** numkeys key \[key ...\] \[WEIGHTS weight \[weight ...\]\] \[AGGREGATE SUM \| MIN \| MAX\] \[WITHSCORES\]  
With WITHSCORE, list/string  
Without WITHSCORE, set/string  
**ZINTERCARD** numkeys key \[key ...\] \[LIMIT limit\]  
numeric/string  
**ZINTERSTORE** destination numkeys key \[key ...\] \[WEIGHTS weight \[weight ...\]\] \[AGGREGATE SUM \| MIN \| MAX\]  
numeric/string  
**ZLEXCOUNT** key min max  
numeric  
**ZMPOP** numkeys key \[key ...\] MIN \| MAX \[COUNT count\]  
numeric/string  
**ZMPOP** numkeys key \[key ...\] MIN \| MAX \[COUNT count\]  
list/string  
**ZMSCORE** key member \[member ...\]  
list/string  
**ZPOPMAX** key \[count\]  
list/string  
**ZPOPMIN** key \[count\]  
list/string  
**ZRANDMEMBER** key \[ count \[WITHSCORES\]\]  
list/string  
**ZRANGE** key start stop \[ BYSCORE \| BYLEX\] \[REV\] \[LIMIT offset count\] \[WITHSCORES\]  
set/string  
**ZRANGESTORE** dst src min max \[ BYSCORE \| BYLEX\] \[REV\] \[LIMIT offset count\]  
numeric/string  
**ZRANK** key member  
string/null  
**ZREM** key member \[member ...\]  
numeric/string  
**ZREMRANGEBYLEX** key min max  
numeric/string  
**ZREMRANGEBYRANK** key start stop  
numeric/string  
**ZREMRANGEBYSCORE** key min max  
numeric/string  
**ZREVRANK** key member  
numeric/string  
**ZSCAN** key cursor \[MATCH pattern\] \[COUNT count\]  
list/string  
**ZSCORE** key member  
numeric/string  
**ZUNION** numkeys key \[key ...\] \[WEIGHTS weight \[weight ...\]\] \[AGGREGATE SUM \| MIN \| MAX\] \[WITHSCORES\]  
list/string  
**ZUNIONSTORE** destination numkeys key \[key ...\] \[WEIGHTS weight \[weight ...\]\] \[AGGREGATE SUM \| MIN \| MAX\]  
numeric/string  