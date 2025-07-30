update DbColumn    set active=false where active and db_table_id in(select id from DbTable where active and db_database_id in (select id from DbDatabase where active and db_server_id=?{db_server_id}));
update DbObject    set active=false where active and db_database_id in (select id from DbDatabase where active and db_server_id=?{db_server_id});
update DbPrivilege set active=false where active and db_database_id in (select id from DbDatabase where active and db_server_id=?{db_server_id});
update DbTable     set active=false where active and db_database_id in (select id from DbDatabase where active and db_server_id=?{db_server_id});
update DbDatabase  set active=false where active and db_server_id=?{db_server_id};
