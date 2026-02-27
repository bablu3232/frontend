import mysql.connector

conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="",
    database="drugssearch"
)
cursor = conn.cursor()
cursor.execute("SELECT parameter_name FROM lab_parameters WHERE parameter_name LIKE '%SGPT%' OR parameter_name LIKE '%SGOT%'")
rows = cursor.fetchall()
for row in rows:
    print(row[0])
conn.close()
