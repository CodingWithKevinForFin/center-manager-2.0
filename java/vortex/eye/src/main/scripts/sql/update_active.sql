UPDATE ??{table_name} set active=?{active} where active!=?{active} and ??{id_column}  in (?{ids})
