package edu.uw.medhas.mhealthapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.uw.medhas.mhealthapp.model.SecureAnnotatedModel;
import edu.uw.medhas.mhealthapp.model.SecureSerializableModel;
import edu.uw.medhas.mhealthsecurityframework.password.PasswordUtils;
import edu.uw.medhas.mhealthsecurityframework.password.exception.PasswordNoLowerCaseCharacterException;
import edu.uw.medhas.mhealthsecurityframework.password.exception.PasswordNoNumberCharacterException;
import edu.uw.medhas.mhealthsecurityframework.password.exception.PasswordNoSpecialCharacterException;
import edu.uw.medhas.mhealthsecurityframework.password.exception.PasswordNoUpperCaseCharacterException;
import edu.uw.medhas.mhealthsecurityframework.password.exception.PasswordTooShortException;
import edu.uw.medhas.mhealthsecurityframework.storage.cache.SecureCacheHandler;
import edu.uw.medhas.mhealthsecurityframework.storage.internal.SecureInternalFileHandler;
import edu.uw.medhas.mhealthsecurityframework.storage.external.SecureExternalFileHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SecureInternalFileHandler mSecureInternalFileHandler = null;
    private SecureExternalFileHandler mSecureExternalFileHandler = null;
    private SecureCacheHandler mSecureCacheHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSecureInternalFileHandler = new SecureInternalFileHandler(getBaseContext());
        mSecureExternalFileHandler = new SecureExternalFileHandler(getBaseContext());
        mSecureCacheHandler = new SecureCacheHandler(getBaseContext());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        View newView = null;
        final LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (id == R.id.nav_password) {
            newView = inflater.inflate(R.layout.content_pwstrchecker, null);
        } else if (id == R.id.nav_cache_serializable) {
            newView = inflater.inflate(R.layout.content_cacsto_slz, null);
        } else if (id == R.id.nav_cache_annotation) {
            newView = inflater.inflate(R.layout.content_cacsto_ano, null);
        } else if (id == R.id.nav_internal_serializable) {
            newView = inflater.inflate(R.layout.content_intsto_slz, null);
        } else if (id == R.id.nav_internal_annotation) {
            newView = inflater.inflate(R.layout.content_intsto_ano, null);
        } else if (id == R.id.nav_external_serializable) {
            newView = inflater.inflate(R.layout.content_extsto_slz, null);
        } else if (id == R.id.nav_external_annotation) {
            newView = inflater.inflate(R.layout.content_extsto_ano, null);
        }

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_container);
        mainLayout.removeAllViews();
        mainLayout.addView(newView);

        if (id == R.id.nav_password) {
            final EditText editTextPw = (EditText) findViewById(R.id.password);
            final Button btnCreateAccount = (Button) findViewById(R.id.createAccount);

            btnCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String passwordStr = editTextPw.getText().toString();

                    try {
                        PasswordUtils.validatePassword(passwordStr);
                        Toast.makeText(getApplicationContext(), "Password is strong", Toast.LENGTH_LONG).show();
                    } catch (PasswordTooShortException ptsex) {
                        Toast.makeText(getApplicationContext(), "Password is too small", Toast.LENGTH_LONG).show();
                    } catch (PasswordNoUpperCaseCharacterException pnuccex) {
                        Toast.makeText(getApplicationContext(), "Password has no upper case character",
                                Toast.LENGTH_LONG).show();
                    } catch (PasswordNoLowerCaseCharacterException pnlccex) {
                        Toast.makeText(getApplicationContext(), "Password has no lower case character",
                                Toast.LENGTH_LONG).show();
                    } catch (PasswordNoNumberCharacterException pnncex) {
                        Toast.makeText(getApplicationContext(), "Password has no number", Toast.LENGTH_LONG).show();
                    } catch (PasswordNoSpecialCharacterException pnscex) {
                        Toast.makeText(getApplicationContext(), "Password has no special character",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (id == R.id.nav_cache_serializable) {
            final EditText editTextInp = (EditText) findViewById(R.id.cacSlzSensitiveInp);
            final Button btnStore = (Button) findViewById(R.id.cacSlzStore);
            final TextView editTextOp = (TextView) findViewById(R.id.cacSlzSensitiveOp);
            final Button btnRetrieve = (Button) findViewById(R.id.cacSlzRetrieve);

            btnStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureSerializableModel ssm = new SecureSerializableModel(editTextInp.getText().toString());
                        mSecureCacheHandler.writeData(ssm, "cachestorage-serializable.txt");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error storing file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            btnRetrieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureSerializableModel ssm = mSecureCacheHandler.readData(SecureSerializableModel.class,
                                "cachestorage-serializable.txt");
                        editTextOp.setText(ssm.getData());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error retrieving file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

        } else if (id == R.id.nav_cache_annotation) {

            final EditText editTextInp = (EditText) findViewById(R.id.cacAnoSensitiveInp);
            final Button btnStore = (Button) findViewById(R.id.cacAnoStore);
            final TextView editTextOp = (TextView) findViewById(R.id.cacAnoSensitiveOp);
            final Button btnRetrieve = (Button) findViewById(R.id.cacAnoRetrieve);

            btnStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureAnnotatedModel ssm = new SecureAnnotatedModel();
                        ssm.setData(editTextInp.getText().toString());
                        mSecureCacheHandler.writeData(ssm, "cachestorage-annotation.txt");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error storing file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            btnRetrieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureAnnotatedModel ssm = mSecureCacheHandler.readData(SecureAnnotatedModel.class,
                                "cachestorage-annotation.txt");
                        editTextOp.setText(ssm.getData());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error retrieving file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

        } else if (id == R.id.nav_internal_serializable) {
            final EditText editTextInp = (EditText) findViewById(R.id.intSlzSensitiveInp);
            final Button btnStore = (Button) findViewById(R.id.intSlzStore);
            final TextView editTextOp = (TextView) findViewById(R.id.intSlzSensitiveOp);
            final Button btnRetrieve = (Button) findViewById(R.id.intSlzRetrieve);

            btnStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureSerializableModel ssm = new SecureSerializableModel(editTextInp.getText().toString());
                        mSecureInternalFileHandler.writeData(ssm, "internalstorage-serializable.txt");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error storing file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            btnRetrieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureSerializableModel ssm = mSecureInternalFileHandler.readData(SecureSerializableModel.class,
                                "internalstorage-serializable.txt");
                        editTextOp.setText(ssm.getData());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error retrieving file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

        } else if (id == R.id.nav_internal_annotation) {

            final EditText editTextInp = (EditText) findViewById(R.id.intAnoSensitiveInp);
            final Button btnStore = (Button) findViewById(R.id.intAnoStore);
            final TextView editTextOp = (TextView) findViewById(R.id.intAnoSensitiveOp);
            final Button btnRetrieve = (Button) findViewById(R.id.intAnoRetrieve);

            btnStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureAnnotatedModel ssm = new SecureAnnotatedModel();
                        ssm.setData(editTextInp.getText().toString());
                        mSecureInternalFileHandler.writeData(ssm, "internalstorage-annotation.txt");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error storing file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            btnRetrieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureAnnotatedModel ssm = mSecureInternalFileHandler.readData(SecureAnnotatedModel.class,
                                "internalstorage-annotation.txt");
                        editTextOp.setText(ssm.getData());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error retrieving file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

        } else if (id == R.id.nav_external_serializable) {
            final EditText editTextInp = (EditText) findViewById(R.id.extSlzSensitiveInp);
            final Button btnStore = (Button) findViewById(R.id.extSlzStore);
            final TextView editTextOp = (TextView) findViewById(R.id.extSlzSensitiveOp);
            final Button btnRetrieve = (Button) findViewById(R.id.extSlzRetrieve);

            btnStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureSerializableModel ssm = new SecureSerializableModel(editTextInp.getText().toString());
                        mSecureExternalFileHandler.writeData(ssm, Environment.DIRECTORY_DOCUMENTS,
                                "externalstorage-serializable.txt");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error storing file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            btnRetrieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureSerializableModel ssm = mSecureExternalFileHandler.readData(SecureSerializableModel.class,
                                Environment.DIRECTORY_DOCUMENTS,
                                "externalstorage-serializable.txt");
                        editTextOp.setText(ssm.getData());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error retrieving file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

        } else if (id == R.id.nav_external_annotation) {
            final EditText editTextInp = (EditText) findViewById(R.id.extAnoSensitiveInp);
            final Button btnStore = (Button) findViewById(R.id.extAnoStore);
            final TextView editTextOp = (TextView) findViewById(R.id.extAnoSensitiveOp);
            final Button btnRetrieve = (Button) findViewById(R.id.extAnoRetrieve);

            btnStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureAnnotatedModel ssm = new SecureAnnotatedModel();
                        ssm.setData(editTextInp.getText().toString());
                        mSecureExternalFileHandler.writeData(ssm, Environment.DIRECTORY_DOCUMENTS,
                                "externalstorage-annotation.txt");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error storing file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            btnRetrieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SecureAnnotatedModel ssm = mSecureExternalFileHandler.readData(SecureAnnotatedModel.class
                                , Environment.DIRECTORY_DOCUMENTS, "externalstorage-annotation.txt");
                        editTextOp.setText(ssm.getData());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error retrieving file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
