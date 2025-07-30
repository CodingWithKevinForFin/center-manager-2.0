# AMI Docs README

This README covers setting the docs locally and some development tips. If you run into any issues please contact Joe.

## SETUP

This expects you have a python install, ideally 3.10.X

1. Open a terminal, cd to this directory a.k.a. `YOUR_BRANCH/java/3forge/documentation`

2. Install python if not installed then run `python -m venv ~/venv/docs` to install a global venv for use across all branches or run `python -m venv ./venv` for a local venv for this branch

3. Activate your venv with `PATH_TO_VENV\Scripts\activate.bat` for windows and `. PATH_TO_VENV/bin/activate` for linux

4. Run `pip install --use-pep517 -r requirements.txt`. Note that the requirements.txt is located in `documentation/ami` directory

5. cd to `ami` then run `mkdocs serve`

6. (Optional) Deactivate venv by with `PATH_TO_VENV/Scripts/deactivate.bat`

## Working again

To work on the docs again repeat steps 3/4/5 (note that 4 can be skipped if `requirements.txt` and the `/lexer/` directory haven't changed)


## Deployment

1. Log in to Jenkins: http://fire.3forge.net:8080/job/AMI_Documentation/

2. Click on Build with Parameters

3. Put in the version that you want to build then hit Build

Takes about 5 min to finish building

## ECLIPSE SETUP

1. Right click on the Package Explorer, select Import, then Projects from Perforce

2. Set up your Perforce connection, then only select the directory this `readme.md` file is in

3. (Optional) Go to Project > Properties then in the Resource menu set Text File Encoding to Other and select UTF-8. (If you have trouble saving changes for .md files)

## DEVELOPMENT TIPS

- .md files are for each page, mkdocs.yml is the configuration YAML, all resources go in /resources/

- Use markdown wherever possible, try to never write HTML

- If you want to use an extension which requires, please run `pip freeze > requirements.txt` after installing the new

## Useful markdown tools

- Markdown Cheatsheet [link](https://www.markdownguide.org/cheat-sheet/)

- MkDocs Cheatsheet [link](https://yakworks.github.io/docmark/cheat-sheet/)

- Table generator [link](https://www.tablesgenerator.com/markdown_tables)

- Pandocs for converting between markdown versions, make sure to set the wrapping to `No wrap` [link](https://pandoc.org/try/)

- The extended ASCII codes table (character code 128-255) to make sure we aren't including any bad symbols [link](https://www.ascii-code.com/), particularly the following:

```
Ã¢â‚¬Â¦Ã¢â‚¬ËœÃ¢â‚¬â„¢Ã¢â‚¬Å“Ã¢â‚¬ï¿½Ã¢â‚¬Â¢Ã¢â‚¬â€œÃ¢â‚¬â€�
```

## Sample great docs

- The documentation software we use [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/)

- The database [kx](https://code.kx.com/home/) (also made with Materila for MkDocs)

- The python package [pandas](https://pandas.pydata.org/docs/index.html)

- The database [MongoDB](https://www.mongodb.com/docs/manual/)
