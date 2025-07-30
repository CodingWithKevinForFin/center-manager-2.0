class AmiClientCommandDef:

    def __init__(self, id, name, argumentsjson=None, whereclause="True", help=None, enabledexpression="True", fields=None, filterclause=None, selectmode=None, style=None, conditions=None, level=None, priority=None):
        self.id = id
        self.name = name
        self.argumentsjson  = argumentsjson
        self.whereclause = whereclause
        self.help = help
        self.enabledexpression = enabledexpression
        self.fields = fields
        self.filterclause = filterclause
        self.selectmode = selectmode
        self.style = style
        self.conditions = conditions
        self.level = level
        self.priority = priority