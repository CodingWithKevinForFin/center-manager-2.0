INSERT INTO Press_Release(
	id,
	created_on,
	email,
	first_name,
	last_name,
	company,
	phone,
	contact_phone,
	contact_email
) VALUES (
	?{id},
	?{createdOn},
	?{email},
	?{firstName},
	?{lastName},
	?{company},
	?{phone},
	?{contactPhone},
	?{contactEmail}
);
