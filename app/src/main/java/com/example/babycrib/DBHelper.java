package com.example.babycrib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context){
        super(context, "baby.db", null, 1);
    }
    public void onCreate(SQLiteDatabase db){

        db.execSQL("create table lullaby (id int primary key, title text, playtime text, composer text, preference int)");
        db.execSQL("INSERT INTO lullaby VALUES(1, '핑크퐁 자장가', '00:03:14','김민지', 1)");
        db.execSQL("INSERT INTO lullaby VALUES(2, '슈만 자장가', '00:03:30','김동겸', 2)");
        db.execSQL("INSERT INTO lullaby VALUES(3, '섬집아기', '00:04:39','윤석준', 6)");
        db.execSQL("INSERT INTO lullaby VALUES(4, '브람스 자장가', '00:04:16','백규열', 5)");
        db.execSQL("INSERT INTO lullaby VALUES(5, '캐롤', '00:03:13','안교민', 2)");
        db.execSQL("INSERT INTO lullaby VALUES(6, '모짜르트 자장가', '00:04:14','구동주', 7)");
        db.execSQL("INSERT INTO lullaby VALUES(7, '오르골 자장가', '00:03:20','김은지', 1)");
        db.execSQL("INSERT INTO lullaby VALUES(8, '잠자는 아기', '00:03:42','최소희', 3)");
        db.execSQL("INSERT INTO lullaby VALUES(9, '명상음악', '00:03:38','김연수', 0)");
        db.execSQL("INSERT INTO lullaby VALUES(10, '아기를 위한 클래식','00:04:32', '정수경',7)");

        db.execSQL("create table behaivor (id int primary key , name text, cnt int)");
        db.execSQL("INSERT INTO behaivor VALUES(1, '기저귀',  4)");
        db.execSQL("INSERT INTO behaivor VALUES(2, '분유',  8)");
        db.execSQL("INSERT INTO behaivor VALUES(3, '옹알이',  3)");
        db.execSQL("INSERT INTO behaivor VALUES(4, '울기', 7)");
        db.execSQL("INSERT INTO behaivor VALUES(5, '칭얼칭얼',  6)");

        db.execSQL("create table seat (id integer primary key autoincrement, name text, step int)");
        // 시트온도
        db.execSQL("INSERT INTO seat values(null, '시트온도', '3')");
        db.execSQL("INSERT INTO seat values(null, '시트온도', '3')");
        db.execSQL("INSERT INTO seat values(null, '시트온도', '3')");
        db.execSQL("INSERT INTO seat values(null, '시트온도', '0')");
        db.execSQL("INSERT INTO seat values(null, '시트온도', '2')");
        db.execSQL("INSERT INTO seat values(null, '시트온도', '3')");
        db.execSQL("INSERT INTO seat values(null, '시트온도', '1')");
        db.execSQL("INSERT INTO seat values(null, '시트온도', '1')");
        db.execSQL("INSERT INTO seat values(null, '시트온도', '3')");

        db.execSQL("create table sleepdiary (id integer primary key autoincrement, sleepdate text,wakedate text, sleeptime text, waketime text, sep text, sleepingtime int)");

        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-16', '2019-11-17', '22:28:00' , '08:20:07' , '밤' ,600)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-16', '2019-11-16', '14:22:23' , '16:30:42' ,'낮', 132)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-15', '2019-11-16', '22:15:23', '09:32:14', '밤', 643)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-15', '2019-11-15', '13:07:42', '16:13:44', '낮', 186)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-15', '2019-11-15', '09:51:10', '11:25:29', '낮', 94)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-14', '2019-11-15', '21:52:06', '04:46:10', '밤', 186)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-14', '2019-11-14', '13:07:42', '16:13:44', '낮', 186)");

        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-13', '2019-11-14', '20:33:18', '05:47:31', '밤', 554)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-12', '2019-11-12', '16:07:42', '18:13:09', '낮', 126)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-12', '2019-11-12', '12:12:11', '14:56:00', '낮', 166)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-11', '2019-11-12', '23:41:31', '03:44:21', '밤', 303)");

        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-11', '2019-11-11', '13:07:42', '16:13:44', '낮', 186)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-10', '2019-11-11', '22:50:17', '05:06:59', '밤', 382)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-09', '2019-11-10', '21:07:42', '09:41:31', '밤', 753)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-08', '2019-11-09', '23:25:31', '06:42:01', '밤', 436)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-08', '2019-11-08', '13:07:42', '16:13:44', '낮', 186)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-08', '2019-11-08', '03:11:33', '07:13:56', '밤', 242)");

        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-07', '2019-11-07', '21:08:17', '21:58:03', '밤', 50)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-07', '2019-11-07', '15:00:02' ,'16:30:08','낮' ,90)");
        db.execSQL("INSERT INTO sleepdiary VALUES(NULL, '2019-11-07', '2019-11-07', '11:28:00' , '12:39:48' , '낮' , 71)");
    }
    public void onUpgrade(SQLiteDatabase db, int old, int newv){
        db.execSQL("drop table if exists users");
        onCreate(db);
    }
}
