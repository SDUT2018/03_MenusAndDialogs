package edu.rosehulman.fisherds.pointofsale;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private Item mCurrentItem;
  private Item mClearedItem;
  private List<Item> mItems;

  private TextView mNameTextView, mQuantityTextView, mDateTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mItems = new ArrayList<>();

    // For testing only, add a bunch of hardcoded data.
    mItems.add(new Item("Example 1", 100, new GregorianCalendar()));
    mItems.add(new Item("Example 2", 101, new GregorianCalendar()));
    mItems.add(new Item("Example 3", 102, new GregorianCalendar()));
    mItems.add(new Item("Example 4", 103, new GregorianCalendar()));

    mNameTextView = findViewById(R.id.name_text);
    registerForContextMenu(mNameTextView);
    mQuantityTextView = findViewById(R.id.quantity_text);
    mDateTextView = findViewById(R.id.date_text);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        addEditItem(false);
      }
    });
  }

  private void addEditItem(final boolean isEdit) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View view = getLayoutInflater().inflate(R.layout.item_dialog,
        null, false);
    builder.setView(view);
    final EditText nameEditText = view.findViewById(R.id.item_name_edit_text);
    final EditText quantityEditText = view.findViewById(R.id.item_quantity_edit_text);
    final CalendarView deliveryDateView = view.findViewById(R.id.item_calendar_view);

    if (isEdit) {
      nameEditText.setText(mCurrentItem.getName());
      quantityEditText.setText("" + mCurrentItem.getQuantity());
      deliveryDateView.setDate(mCurrentItem.getDeliveryDateTime());
    }

    final GregorianCalendar calendar = new GregorianCalendar();
    deliveryDateView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
      @Override
      public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
      }
    });

    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String name = nameEditText.getText().toString();
        int quantity = Integer.parseInt(quantityEditText.getText().toString());

        if (isEdit) {
          mCurrentItem.setName(name);
          mCurrentItem.setQuantity(quantity);
          mCurrentItem.setDeliveryDate(calendar);
        } else {
          mCurrentItem = new Item(name, quantity, calendar);
          mItems.add(mCurrentItem);
        }

        updateView();
      }
    });
    builder.setNegativeButton(android.R.string.cancel, null);
    builder.create().show();
  }

  private String[] getNames() {
    String[] names = new String[mItems.size()];
    for (int i = 0; i < mItems.size(); i++) {
      names[i] = mItems.get(i).getName();
    }
    return names;
  }

  private void updateView() {
    mNameTextView.setText(mCurrentItem.getName());
    mQuantityTextView.setText(
        getString(R.string.quantity_format, mCurrentItem.getQuantity()));
    mDateTextView.setText(
        getString(R.string.date_format, mCurrentItem.getDeliveryDateString()));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
//        startActivity(new Intent(Settings.ACTION_SETTINGS));
        startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
        return true;
      case R.id.action_reset:
        mClearedItem = mCurrentItem;
        mCurrentItem = Item.getEmptyItem();
        updateView();
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout), R.string.item_cleared, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mCurrentItem = mClearedItem;
            mClearedItem = null;
            updateView();
            Snackbar.make(findViewById(R.id.coordinator_layout), R.string.item_restored, Snackbar.LENGTH_LONG).show();
          }
        });
        snackbar.show();
        return true;
      case R.id.action_search_history:
        showSearchDialog();
        return true;
      case R.id.action_clear_all:
        showClearAllDialog();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showClearAllDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.choose_an_item);
    builder.setMessage(R.string.are_you_sure);
    builder.setNegativeButton(android.R.string.cancel, null);
    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        mCurrentItem = Item.getEmptyItem();
        mItems.clear();
        updateView();
      }
    });
    builder.create().show();
  }

  private void showSearchDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.choose_an_item);

    builder.setItems(getNames(), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        mCurrentItem = mItems.get(which);
        updateView();
      }
    });
    builder.setNegativeButton(android.R.string.cancel, null);
    builder.create().show();
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    getMenuInflater().inflate(R.menu.menu_context, menu);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_context_edit:
        addEditItem(true);
        return true;
      case R.id.menu_context_remove:
        mItems.remove(mCurrentItem);
        mCurrentItem = Item.getEmptyItem();
        updateView();
        return true;
    }

    return super.onContextItemSelected(item);
  }
}
