package com.example.crazyflower.dateremember.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseManager {

    public static final int ITERATE_FUTURE_OK = 1;

    public static final int ITERATE_FUTURE_ERROR = -1;

    public static final int ADD_EVENT_OK = 2;

    public static final int ADD_EVENT_ERROR = -2;

    public static final int FIND_EVENT_OK = 3;

    public static final int FIND_EVENT_ERROR = -3;

    public static final int UPDATE_EVENT_OK = 4;

    public static final int UPDATE_EVENT_ERROR = -4;

    public static final int UPDATE_OK = 5;

    protected Context context;

    public DatabaseManager(Context context) {
        this.context = context;
    }

    public void getFutureEventList(Handler handler) {
        AsyncTask<Object, Object, List<Event>> asyncTask = new IterateDatabaseAsyncTask(handler, DateRememberDatabaseHelper.FUTURE_EVENT_TABLE_NAME);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
    }

    public void addFutureEventByAttributes(Handler handler, long dateMILLS, String note, int reminderIndex) {
        AsyncTask<Object, Object, Long> asyncTask = new AddEventAsyncTask(handler, Event.EventType.FUTURE_EVENT, DateRememberDatabaseHelper.FUTURE_EVENT_TABLE_NAME ,dateMILLS, note, reminderIndex);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
    }

    public void findFutureEventById(Handler handler, long id) {
        AsyncTask<Object, Object, Event> asyncTask = new FindEventAsyncTask(handler, DateRememberDatabaseHelper.FUTURE_EVENT_TABLE_NAME, id);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
    }

    public void updateFutureEventByAttributes(Handler handler, long id, long dateMILLS, String note, int reminderIndex) {
        AsyncTask<Object, Object, Object> asyncTask = new UpdateEventAsyncTask(handler, DateRememberDatabaseHelper.FUTURE_EVENT_TABLE_NAME, id, dateMILLS, note, reminderIndex);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
    }

    public void updateDatabase(Handler handler) {
        UpdateDatabaseAsyncTask asyncTask = new UpdateDatabaseAsyncTask(handler);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
    }

    private class FindEventAsyncTask extends AsyncTask<Object, Object, Event> {

        Handler handler;

        String tableName;

        long id;

        FindEventAsyncTask(Handler handler, String tableName, long id) {
            this.handler = handler;
            this.tableName = tableName;
            this.id = id;
        }

        @Override
        protected Event doInBackground(Object... objects) {
            DateRememberDatabaseHelper helper = new DateRememberDatabaseHelper(DatabaseManager.this.context);
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE " + DateRememberDatabaseHelper.EVENT_COLUMN_ID + "=?", new String[] {String.valueOf(id)});
            if (1 == cursor.getCount()) {
                cursor.moveToFirst();
                long mills = cursor.getLong(cursor.getColumnIndex(DateRememberDatabaseHelper.EVENT_COLUMN_DATE_MILLS));
                String note = cursor.getString(cursor.getColumnIndex(DateRememberDatabaseHelper.EVENT_COLUMN_NOTE));
                int reminderIndex = cursor.getInt(cursor.getColumnIndex(DateRememberDatabaseHelper.EVENT_COLUMN_REMINDER_INDEX));
                return new FutureEvent(id, mills, note, reminderIndex);
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Event event) {
            super.onPostExecute(event);
            Message message = handler.obtainMessage();
            if (null == event)
                message.what = FIND_EVENT_ERROR;
            else {
                message.what = FIND_EVENT_OK;
                message.obj = event;
            }
            message.sendToTarget();
        }
    }

    private class IterateDatabaseAsyncTask extends AsyncTask<Object, Object, List<Event>> {

        Handler handler;

        String tableName;

        private IterateDatabaseAsyncTask(Handler handler, String tableName) {
            this.handler = handler;
            this.tableName = tableName;
        }

        @Override
        protected List<Event> doInBackground(Object... objects) {
            List<Event> events = new ArrayList<>();
            DateRememberDatabaseHelper helper = new DateRememberDatabaseHelper(DatabaseManager.this.context);
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.query(DateRememberDatabaseHelper.FUTURE_EVENT_TABLE_NAME, null, null, null, null, null, DateRememberDatabaseHelper.EVENT_COLUMN_DATE_MILLS);
            long id;
            long dateMills;
            String note;
            int reminderIndex;
            while (cursor.moveToNext()) {
                id = cursor.getLong(cursor.getColumnIndex(DateRememberDatabaseHelper.EVENT_COLUMN_ID));
                dateMills = cursor.getLong(cursor.getColumnIndex(DateRememberDatabaseHelper.EVENT_COLUMN_DATE_MILLS));
                note = cursor.getString(cursor.getColumnIndex(DateRememberDatabaseHelper.EVENT_COLUMN_NOTE));
                reminderIndex = cursor.getInt(cursor.getColumnIndex(DateRememberDatabaseHelper.EVENT_COLUMN_REMINDER_INDEX));
                events.add(new FutureEvent(id, dateMills, note, reminderIndex));
            }
            cursor.close();
            database.close();
            helper.close();
            return events;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            super.onPostExecute(events);
            Message message = handler.obtainMessage(ITERATE_FUTURE_OK);
            message.obj = events;
            message.sendToTarget();
        }


    }

    private class AddEventAsyncTask extends AsyncTask<Object, Object, Long> {

        Handler handler;

        Event.EventType type;

        String tableName;

        long dateMILLS;

        String note;

        int reminderIndex;

        private AddEventAsyncTask(Handler handler, Event.EventType type, String tableName, long dateMILLS, String note, int reminderIndex) {
            this.handler = handler;
            this.type = type;
            this.tableName = tableName;
            this.dateMILLS = dateMILLS;
            this.note = note;
            this.reminderIndex = reminderIndex;
        }

        @Override
        protected Long doInBackground(Object... objects) {
            DateRememberDatabaseHelper helper = new DateRememberDatabaseHelper(DatabaseManager.this.context);

            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DateRememberDatabaseHelper.EVENT_COLUMN_DATE_MILLS, dateMILLS);
            contentValues.put(DateRememberDatabaseHelper.EVENT_COLUMN_NOTE, note);
            contentValues.put(DateRememberDatabaseHelper.EVENT_COLUMN_REMINDER_INDEX, reminderIndex);
            long rowIndex = database.insert(tableName, null, contentValues);
            database.close();
            return rowIndex;
        }

        @Override
        protected void onPostExecute(Long rowIndex) {
            super.onPostExecute(rowIndex);
            Message message = handler.obtainMessage(ADD_EVENT_OK);
            switch (type) {
                case FUTURE_EVENT:
                    message.obj = new FutureEvent(rowIndex, dateMILLS, note, reminderIndex);
                    break;
                case PREVIOUS_EVENT:
            }
            message.sendToTarget();
        }
    }

    private class UpdateEventAsyncTask extends AsyncTask<Object, Object, Object> {

        private Handler handler;

        private String tableName;

        private long id;

        private long dateMills;

        private String note;

        private int reminderIndex;

        UpdateEventAsyncTask(Handler handler, String tableName, long id, long dateMills, String note, int reminderIndex) {
            this.handler = handler;
            this.tableName = tableName;
            this.id = id;
            this.dateMills = dateMills;
            this.note = note;
            this.reminderIndex = reminderIndex;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            DateRememberDatabaseHelper helper = new DateRememberDatabaseHelper(DatabaseManager.this.context);
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DateRememberDatabaseHelper.EVENT_COLUMN_DATE_MILLS, dateMills);
            contentValues.put(DateRememberDatabaseHelper.EVENT_COLUMN_NOTE, note);
            contentValues.put(DateRememberDatabaseHelper.EVENT_COLUMN_REMINDER_INDEX, reminderIndex);
            database.update(tableName, contentValues, "id=?", new String[] {String.valueOf(id)});
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Message message = handler.obtainMessage();
            message.what = UPDATE_EVENT_OK;
            message.sendToTarget();
        }
    }

    private class UpdateDatabaseAsyncTask extends AsyncTask<Object, Object, Object> {

        Handler handler;

        UpdateDatabaseAsyncTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long s = calendar.getTimeInMillis();
            DateRememberDatabaseHelper helper = new DateRememberDatabaseHelper(DatabaseManager.this.context);
            SQLiteDatabase database = helper.getWritableDatabase();
            Cursor cursor = database.query(DateRememberDatabaseHelper.FUTURE_EVENT_TABLE_NAME, null, null, null, null, null, DateRememberDatabaseHelper.EVENT_COLUMN_DATE_MILLS);
            while (cursor.moveToNext()) {
                if (cursor.getLong(cursor.getColumnIndex(DateRememberDatabaseHelper.EVENT_COLUMN_DATE_MILLS)) < s) {
                    database.delete(DateRememberDatabaseHelper.FUTURE_EVENT_TABLE_NAME, "id=?", new String[] {String.valueOf(cursor.getLong(cursor.getColumnIndex(DateRememberDatabaseHelper.EVENT_COLUMN_ID)))} );
                } else {
                    break;
                }
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Message message = handler.obtainMessage();
            message.what = UPDATE_OK;
            message.sendToTarget();
        }
    }
}
