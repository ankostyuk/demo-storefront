from commands import getstatusoutput
from variables import variables

def execute_script(script_name):
    # username derived from schema name
    status, text = getstatusoutput(
        ('psql -1 -w -v ON_ERROR_STOP=1 -h %(host)s -p %(port)s -d %(database)s -U %(schema)s -f ../sql/'+ script_name) % variables)
    print status, text