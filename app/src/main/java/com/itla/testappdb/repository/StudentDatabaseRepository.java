package com.itla.testappdb.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itla.testappdb.database.connection.DatabaseConnection;
import com.itla.testappdb.entity.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentDatabaseRepository implements CrudRepository<Student, Integer> {

    private DatabaseConnection connection;
    private static final String TABLE_NAME = "students";

    public StudentDatabaseRepository(Context context) {
        this.connection = new DatabaseConnection(context);
    }

    @Override
    public Student create(Student entity) {
        final SQLiteDatabase sqLiteDatabase = this.connection.getWritableDatabase();
        final ContentValues contentValues = entity.contentValues();

        long id = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.i("StudentRepository", "Unknown error have pass trying insert student");
        } else {
            Log.i("StudentRepository", String.format("The student have been created with id %d", id));
            entity.setId((int) id);
        }

        sqLiteDatabase.close();

        return entity;
    }

    @Override
    public void update(Student entity) {
        final SQLiteDatabase sqLiteDatabase = this.connection.getWritableDatabase();
        final ContentValues contentValues = entity.contentValues();

        sqLiteDatabase.update(TABLE_NAME, contentValues, "id = ?", new String[]{entity.getId().toString()});
        sqLiteDatabase.close();
    }

    @Override
    public void delete(Student entity) {
        final SQLiteDatabase sqLiteDatabase = this.connection.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, "id = ?", new String[]{entity.getId().toString()});
        sqLiteDatabase.close();
    }

    @Override
    public Student get(Integer id) {
        final SQLiteDatabase sqLiteDatabase = this.connection.getReadableDatabase();

        //sqLiteDatabase.query(TABLE_NAME, null, "id = ?", new String[]{id.toString()}, null, null, null);
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT s.id as student_id, s.name as student_name, " +
                "s.registration_number as student_registration_number, " +
                "c.id as career_id, c.name as career_name "+
                "FROM students s " +
                "LEFT JOIN careers c on (c.id = s.career_id) " +
                "WHERE " +
                "s.id = ?", new String[]{id.toString()});

        cursor.moveToFirst();
        Student student = new Student(cursor);
        cursor.close();
        sqLiteDatabase.close();

        return student;
    }

    @Override
    public List<Student> getAll() {
        final SQLiteDatabase sqLiteDatabase = this.connection.getReadableDatabase();
        //final Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null, null);

        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT s.id as student_id, s.name as student_name, " +
                "s.registration_number as student_registration_number, " +
                "c.id as career_id, c.name as career_name " +
                "FROM students s " +
                "LEFT JOIN careers c on (c.id = s.career_id)", null);

        List<Student> students = new ArrayList<>();

        while (cursor.moveToNext()) {
            students.add(new Student(cursor));
        }

        cursor.close();
        sqLiteDatabase.close();

        return students;
    }
}
