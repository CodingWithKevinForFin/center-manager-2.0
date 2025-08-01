INSERT INTO Applicants (
    jobtitle,
    fname,
    lname,
    email,
    phone,
    app_resume,
    cover_letter,
    pronoun,
    pref_fname,
    hear_forge,
    forge_family,
    office_location,
    previous_work,
    sponsorship,
    timestamp
) VALUES (
    ?{jobtitle},
    ?{fname},
    ?{lname},
    ?{email},
    ?{phone},
    ?{app_resume},
    ?{cover_letter},
    ?{pronoun},
    ?{pref_fname},
    ?{hear_forge},
    ?{forge_family},
    ?{office_location},
    ?{previous_work},
    ?{sponsorship},
    ?{timestamp}
);