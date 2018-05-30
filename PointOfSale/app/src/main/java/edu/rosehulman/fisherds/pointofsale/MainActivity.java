package edu.rosehulman.fisherds.pointofsale;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  private Item mCurrentItem;
  private Item mClearedItem;

  private TextView mNameTextView, mQuantityTextView, mDateTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mNameTextView = findViewById(R.id.name_text);
    mQuantityTextView = findViewById(R.id.quantity_text);
    mDateTextView = findViewById(R.id.date_text);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        addItem();
      }
    });
  }

  private void addItem() {
    // Show a dialog to allow a user to create the mCurrentItem

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    // Configure the dialog
    builder.setTitle(R.string.add_new_item);

    builder.setMessage("This is a test only, remove this later");

    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(MainActivity.this,"You clicked ok",
            Toast.LENGTH_SHORT).show();
      }
    });

    builder.create().show();
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
        startActivity(new Intent(Settings.ACTION_SETTINGS));
        return true;
      case R.id.action_reset:
        mClearedItem = mCurrentItem;
        mCurrentItem = Item.getEmptyItem();
        updateView();
        Snackbar snackbar = Snackbar.make(
            findViewById(R.id.coordinator_layout), R.string.item_cleared,
            Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mCurrentItem = mClearedItem;
            mClearedItem = null;
            updateView();
            Snackbar.make(
                findViewById(R.id.coordinator_layout),
                R.string.item_restored,
                Snackbar.LENGTH_LONG).show();
          }
        });
        snackbar.show();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
