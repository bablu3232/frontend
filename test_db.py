import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="",
        database="drugssearch"
    )
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM lab_parameters")
    results = cursor.fetchall()
    conn.close()
    print(f"Loaded {len(results)} parameters")
except Exception as e:
    print(f"Error accessing DB: {e}")
