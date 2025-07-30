"""
    pygments.lexers.zig
    ~~~~~~~~~~~~~~~~~~~

    Lexers for Zig.

    :copyright: Copyright 2006-2021 by the Pygments team, see AUTHORS.
    :license: BSD, see LICENSE for details.
"""
import re
from pygments.lexer import RegexLexer, words, include, bygroups, using, this, combined, default, words
from pygments.token import Comment, Operator, Keyword, Name, String, \
    Number, Text, Punctuation, Whitespace
from pygments.util import shebang_matches
from pygments import unistring as uni

__all__ = ['AmiScriptLexer']


class AmiScriptLexer(RegexLexer):
    """
    lexer for amiscript

    """
    name = 'AmiScript'
    aliases = ['amiscript']
    filenames = ['*.amiscript']
    mimetypes = ['text/amiscript']
    flags = re.MULTILINE | re.DOTALL | re.UNICODE

    amiscript_classes = (
        words(('BigDecimal', 'BigInteger', 'Binary', 'ChartPanel', 'ChartPanelAxis', 'ChartPanelLayer', 'ChartPanelPlot',
           'Collection', 'ColorGradient', 'CommandResponse', 'Complex', 'DashboardResource', 'Datamodel', 'DevTools',
           'DividerPanel', 'Error', 'FilterPanel', 'FormButtonField', 'FormDateField', 'FormDateRangeField', 'FormDateTimeField',
           'FormField', 'FormImageField', 'FormMultiCheckboxField', 'FormMultiSelectField', 'FormPanel', 'FormRadioField', 'FormRangeSliderField',
           'FormSelectField', 'FormSliderField', 'FormTextField', 'FormTimeField', 'FormTimeRangeField', 'FormUploadField', 'HeatmapPanel',
           'Image', 'Iterator', 'Layout', 'List', 'Map', 'Menuitem', 'Panel', 'PdfBuilder', 'Processor', 'Rand', 'Rectangle', 'Relationship',
           'Row', 'RpcRequest', 'RpcResponse', 'ScrollPanel', 'Session', 'Set', 'StyleOptionDefinition', 'StyleSet', 'SurfacePanel', 'SurfacePanelAxis',
           'SurfacePanelLayer', 'Tab', 'Table', 'TablePanel', 'TablePanelColumn', 'TablePanelColumnFilter', 'TableSet', 'TabsPanel', 'TreePanel',
           'TreePanelColumn', 'UTC', 'UTCN', 'UUID', 'Window', 'Session', 'Layout'), prefix=r'(?i)\b', suffix=r'\b'),
        Keyword.Type)

    amisql_keywords = (
        words(('select', 'insert', 'into', 'update', 'delete', 'from', 'where', 'group', 'by', 'order', 'limit', 'offset', 'having', 'as', 'case', 'when', 'end',
            'type', 'left', 'right', 'join', 'only', 'on', 'outer', 'desc', 'asc', 'union', 'create', 'table', 'primary', 'key', 'foreign', 'references', 'default',
            'inner', 'corss', 'natural', 'database', 'drop', 'grant', 'values', 'alter', 'analyze', 'prepare', 'step', 'add', 'rename', 'modify', 'to', 'in',
            'use', 'window', 'partition', 'unpack', 'truncate', 'if', 'not', 'exists', 'delete', 'sync', 'ds', 'procedure', 'trigger', 'timer', 'execute', 'avg',
            'count', 'norm', 'stack', 'median', 'sum', 'except', 'show', 'describe', 'public', 'variable', 'tables', 'temporary'), prefix=r'(?i)\b', suffix=r'\b'),
        Keyword)

    amiscript_primitives = (
        words(('function', 'int', 'float', 'double', 'integer', 'long', 'boolean', 'string', 'byte'), prefix=r'(?i)\b', suffix=r'\b'),
        Keyword.Type)

    amisql_types = (
        words(('numeric', 'decimal', 'date', 'varchar', 'char', 'bigint', 'bit', 'binary', 'text', 'set', 'timestamp', 'number'), prefix=r'(?i)\b', suffix=r'\b'),
        Keyword)

    template = (
        words(('const'), prefix=r'(?i)\b', suffix=r'\b'),
        Keyword)

    control_keywords = (
        words(('break', 'case', 'catch', 'continue', 'switch', 'do', 'else', 'finally', 'for', 'if', 'else', 'in', 'instanceof', 'new', 'return',
            'switch', 'throw', 'try', 'except', 'while', 'concurrent', 'extern', 'virtual'), suffix=r'\b'),
        Keyword)

    constant_keywords = (
        words(('true', 'false', 'null'), suffix=r'\b'),
        Keyword.Constant)

    tokens = {
        'root': [

            # method names (IS NOT WORKING YET)
           # (r'^(\s*(?:[a-zA-Z_][\w.\[\]]*\s+)+?)'  # return arguments
            # r'([a-zA-Z_]\w*)'                      # method name
            # r'(\s*)(\()',                          # signature start
            # bygroups(using(this), Name.Function, Text, Operator)),
            amiscript_classes,
            control_keywords,
            amisql_keywords,
            amiscript_primitives,
            amisql_types,
            constant_keywords,
            
            (r'(?<=\.)*[a-z_A-Z_0-9]+\s*(?=\()', Name.Function), # capture method names
            (r'[^\S\n]+', Text),
            (r'\n', Whitespace),
            (r'\s+', Whitespace),
            (r'//.*?\n', Comment.Single),
            (r'/\*.*?\*/', Comment.Multiline),


            # Floats
            (r'0x[0-9a-fA-F]+\.[0-9a-fA-F]+([pP][\-+]?[0-9a-fA-F]+)?', Number.Float),
            (r'0x[0-9a-fA-F]+\.?[pP][\-+]?[0-9a-fA-F]+', Number.Float),
            (r'[0-9]+\.[0-9]+([eE][-+]?[0-9]+)?', Number.Float),
            (r'[0-9]+\.?[eE][-+]?[0-9]+', Number.Float),

            # Integers
            (r'0b[01]+', Number.Bin),
            (r'0o[0-7]+', Number.Oct),
            (r'0x[0-9a-fA-F]+', Number.Hex),
            (r'[0-9]+', Number.Integer),

            # Identifier
            (r'@[a-zA-Z_]\w*', Name.Builtin),
            (r'[a-zA-Z_]\w*', Name),

            # Characters
            (r'\'\\\'\'', String.Escape),
            (r'\'\\(x[a-fA-F0-9]{2}|u[a-fA-F0-9]{4}|U[a-fA-F0-9]{6}|[nr\\t\'"])\'',
             String.Escape),
            (r'\'[^\\\']\'', String),

            # Strings
            (r'\\\\[^\n]*', String.Heredoc),
            (r'c\\\\[^\n]*', String.Heredoc),
            (r'c?"', String, 'string'),

            # Operators, Punctuation
            (r'[+%=><|^!?/\-*&~:]', Operator),
            (r'[{}()\[\],.;]', Punctuation)
        ],
        'string': [
            (r'\\(x[a-fA-F0-9]{2}|u[a-fA-F0-9]{4}|U[a-fA-F0-9]{6}|[nr\\t\'"])',
             String.Escape),
            (r'[^\\"\n]+', String),
            (r'"', String, '#pop')
        ]
    }
