import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="",
        database="drugssearch"
    )
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT parameter_name FROM lab_parameters WHERE parameter_name LIKE '%ALT%' OR parameter_name LIKE '%ALP%'")
    results = cursor.fetchall()
    conn.close()
    for row in results:
        print(f"'{row['parameter_name']}'")
except Exception as e:
    print(f"Error accessing DB: {e}")
