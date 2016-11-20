package me.palr.palr_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import me.palr.palr_android.api.APIService;
import me.palr.palr_android.models.Message;
import me.palr.palr_android.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maazali on 2016-11-16.
 */
public class ProfileActivity extends AppCompatActivity {

    EditText nameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText ageInput;

    AutoCompleteTextView countryInput;
    AutoCompleteTextView ethnicityInput;

    boolean didChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        nameInput = (EditText) findViewById(R.id.profile_input_name);
        emailInput = (EditText) findViewById(R.id.profile_input_email);
        passwordInput = (EditText) findViewById(R.id.profile_input_password);
        ageInput = (EditText) findViewById(R.id.profile_input_age);
        countryInput = (AutoCompleteTextView) findViewById(R.id.profile_input_country);



        assert (nameInput != null);
        assert (emailInput != null);
        assert (passwordInput != null);
        assert (ageInput != null);
        assert (countryInput != null);

        setupAutoCompleteViews();

        setupUpdateButton();
        addCurrentDataToField();
    }

    private void addCurrentDataToField() {
        User curUser = ((PalrApplication) getApplication()).getCurrentUser();

        nameInput.setText(curUser.getName());
        emailInput.setText(curUser.getEmail());

        if (curUser.getAge() != null)
            ageInput.setText(String.valueOf(curUser.getAge()));

        if (curUser.getCountry() != null)
            countryInput.setText(curUser.getCountry());
    }

    private void setupAutoCompleteViews() {
        ArrayAdapter<String> countryInputAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        countryInput.setThreshold(1);
        countryInput.setAdapter(countryInputAdapter);

        countryInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                    long id) {
                PalrApplication app = (PalrApplication) getApplication();
                User curUser = app.getCurrentUser();

                String newCountry = (String) parent.getAdapter().getItem(position);

                if (newCountry != null && !newCountry.equals(curUser.getCountry())) {
                    curUser.setCountry(newCountry);
                    didChange = true;
                }

                Toast.makeText(ProfileActivity.this, newCountry, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupUpdateButton() {
        AppCompatButton saveBtn = (AppCompatButton) findViewById(R.id.profile_save_btn);

        assert (saveBtn != null);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PalrApplication app = (PalrApplication) getApplication();
                User curUser = app.getCurrentUser();

                updateCurrentUserFields(curUser);

                if (didChange) {
                    makeUserUpdateRequest(curUser);
                } else {
                    Toast.makeText(ProfileActivity.this, "Nothing updated", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateCurrentUserFields(User curUser) {
        Integer age = Integer.parseInt(ageInput.getText().toString().equals("") ? "0" : ageInput.getText().toString());

            if (!age.equals(curUser.getAge()) && age > 0) {
                curUser.setAge(age);
                didChange = true;
            }
    }

    private void makeUserUpdateRequest(User user) {
        final PalrApplication app = (PalrApplication) getApplication();
        APIService service = app.getAPIService();

        Call<User> updateUserReq = service.updateUser(user);

        updateUserReq.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.raw().code() != 200) {
                    Toast.makeText(getApplicationContext(), "Something went wrong, could not update profile!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Profile successfully updated!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong, could not update profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static final String[] ETHNICITICES = {
        "Acholi",
        "Akan",
        "Albanians",
        "Afar",
        "Afrikaaners",
        "Amhara",
        "Arabs",
        "Armenians",
        "Assyrians",
        "Azerbaijanis",
        "Balochis",
        "Bamars",
        "Bambara",
        "Bashkirs",
        "Basques",
        "Belarusians",
        "Bemba",
        "Bengali",
        "Berbers",
        "Beti-Pahuin",
        "Bihari",
        "Bosniaks",
        "Brahui",
        "Bulgarians",
        "Catalans",
        "Chuvash",
        "Circassians",
        "Chewa",
        "Croats",
        "Czechs",
        "Danes",
        "Dinka",
        "Dutch",
        "English",
        "Estonians",
        "Faroese",
        "Finns",
        "French",
        "Frisians",
        "Fula",
        "Ga-Adangbe",
        "Gagauz",
        "Galician",
        "Ganda",
        "Germans",
        "Greeks",
        "Georgians",
        "Gujarati",
        "Han Chinese",
        "Hausa",
        "Hindustani",
        "Hui",
        "Hungarians",
        "Icelanders",
        "Igbo",
        "Ijaw",
        "Irish",
        "Italians",
        "Japanese",
        "Javanese",
        "Jews",
        "Kannada",
        "Kazakhs",
        "Kikuyu",
        "Kongo",
        "Koreans",
        "Kurds",
        "Lebanese",
        "Kyrgyz",
        "Lango",
        "Laz",
        "Luba",
        "Luo",
        "Macedonians",
        "Malays",
        "Malayali",
        "Manchu",
        "Mandinka",
        "Marathi",
        "Moldovans",
        "Mongo",
        "Mongols",
        "Nepali",
        "Norwegians",
        "Nuer",
        "Pashtuns",
        "Oromo",
        "Persians",
        "Poles",
        "Portuguese",
        "Punjabi",
        "Pedi",
        "Romanians",
        "Romani",
        "Russians",
        "Sara",
        "Sardinians",
        "Scottish",
        "Serbs",
        "Shona",
        "Sindhis",
        "Sinhalese",
        "Slovaks",
        "Slovenes",
        "Soga",
        "Somalis",
        "Songhai",
        "Soninke",
        "Sotho",
        "Spaniards",
        "Sundanese",
        "Sukuma",
        "Swazi",
        "Swedes",
        "Tajiks",
        "Tamils",
        "Telugu",
        "Tais",
        "Tibetans",
        "Tswana",
        "Tuaregs",
        "Turks",
        "Turkmens",
        "Ukrainians",
        "Uyghur",
        "Uzbek",
        "Vietnamese",
        "Volga Tatars",
        "Welsh",
        "Xhosa",
        "Yakuts",
        "Yoruba",
        "Zhuang",
        "Zulu"
    };

    public static final String[] COUNTRIES = {
        "Aruba",
        "Afghanistan",
        "Angola",
        "Anguilla",
        "Åland Islands",
        "Albania",
        "Andorra",
        "United Arab Emirates",
        "Argentina",
        "Armenia",
        "American Samoa",
        "Antarctica",
        "French Southern and Antarctic Lands",
        "Antigua and Barbuda",
        "Australia",
        "Austria",
        "Azerbaijan",
        "Burundi",
        "Belgium",
        "Benin",
        "Burkina Faso",
        "Bangladesh",
        "Bulgaria",
        "Bahrain",
        "Bahamas",
        "Bosnia and Herzegovina",
        "Saint Barthélemy",
        "Belarus",
        "Belize",
        "Bermuda",
        "Bolivia",
        "Brazil",
        "Barbados",
        "Brunei",
        "Bhutan",
        "Bouvet Island",
        "Botswana",
        "Central African Republic",
        "Canada",
        "Cocos (Keeling) Islands",
        "Switzerland",
        "Chile",
        "China",
        "Ivory Coast",
        "Cameroon",
        "DR Congo",
        "Republic of the Congo",
        "Cook Islands",
        "Colombia",
        "Comoros",
        "Cape Verde",
        "Costa Rica",
        "Cuba",
        "Curaçao",
        "Christmas Island",
        "Cayman Islands",
        "Cyprus",
        "Czech Republic",
        "Germany",
        "Djibouti",
        "Dominica",
        "Denmark",
        "Dominican Republic",
        "Algeria",
        "Ecuador",
        "Egypt",
        "Eritrea",
        "Western Sahara",
        "Spain",
        "Estonia",
        "Ethiopia",
        "Finland",
        "Fiji",
        "Falkland Islands",
        "France",
        "Faroe Islands",
        "Micronesia",
        "Gabon",
        "United Kingdom",
        "Georgia",
        "Guernsey",
        "Ghana",
        "Gibraltar",
        "Guinea",
        "Guadeloupe",
        "Gambia",
        "Guinea-Bissau",
        "Equatorial Guinea",
        "Greece",
        "Grenada",
        "Greenland",
        "Guatemala",
        "French Guiana",
        "Guam",
        "Guyana",
        "Hong Kong",
        "Heard Island and McDonald Islands",
        "Honduras",
        "Croatia",
        "Haiti",
        "Hungary",
        "Indonesia",
        "Isle of Man",
        "India",
        "British Indian Ocean Territory",
        "Ireland",
        "Iran",
        "Iraq",
        "Iceland",
        "Israel",
        "Italy",
        "Jamaica",
        "Jersey",
        "Jordan",
        "Japan",
        "Kazakhstan",
        "Kenya",
        "Kyrgyzstan",
        "Cambodia",
        "Kiribati",
        "Saint Kitts and Nevis",
        "South Korea",
        "Kosovo",
        "Kuwait",
        "Laos",
        "Lebanon",
        "Liberia",
        "Libya",
        "Saint Lucia",
        "Liechtenstein",
        "Sri Lanka",
        "Lesotho",
        "Lithuania",
        "Luxembourg",
        "Latvia",
        "Macau",
        "Saint Martin",
        "Morocco",
        "Monaco",
        "Moldova",
        "Madagascar",
        "Maldives",
        "Mexico",
        "Marshall Islands",
        "Macedonia",
        "Mali",
        "Malta",
        "Myanmar",
        "Montenegro",
        "Mongolia",
        "Northern Mariana Islands",
        "Mozambique",
        "Mauritania",
        "Montserrat",
        "Martinique",
        "Mauritius",
        "Malawi",
        "Malaysia",
        "Mayotte",
        "Namibia",
        "New Caledonia",
        "Niger",
        "Norfolk Island",
        "Nigeria",
        "Nicaragua",
        "Niue",
        "Netherlands",
        "Norway",
        "Nepal",
        "Nauru",
        "New Zealand",
        "Oman",
        "Pakistan",
        "Panama",
        "Pitcairn Islands",
        "Peru",
        "Philippines",
        "Palau",
        "Papua New Guinea",
        "Poland",
        "Puerto Rico",
        "North Korea",
        "Portugal",
        "Paraguay",
        "Palestine",
        "French Polynesia",
        "Qatar",
        "Réunion",
        "Romania",
        "Russia",
        "Rwanda",
        "Saudi Arabia",
        "Sudan",
        "Senegal",
        "Singapore",
        "South Georgia",
        "Svalbard and Jan Mayen",
        "Solomon Islands",
        "Sierra Leone",
        "El Salvador",
        "San Marino",
        "Somalia",
        "Saint Pierre and Miquelon",
        "Serbia",
        "South Sudan",
        "São Tomé and Príncipe",
        "Suriname",
        "Slovakia",
        "Slovenia",
        "Sweden",
        "Swaziland",
        "Sint Maarten",
        "Seychelles",
        "Syria",
        "Turks and Caicos Islands",
        "Chad",
        "Togo",
        "Thailand",
        "Tajikistan",
        "Tokelau",
        "Turkmenistan",
        "Timor-Leste",
        "Tonga",
        "Trinidad and Tobago",
        "Tunisia",
        "Turkey",
        "Tuvalu",
        "Taiwan",
        "Tanzania",
        "Uganda",
        "Ukraine",
        "United States Minor Outlying Islands",
        "Uruguay",
        "United States",
        "Uzbekistan",
        "Vatican City",
        "Saint Vincent and the Grenadines",
        "Venezuela",
        "British Virgin Islands",
        "United States Virgin Islands",
        "Vietnam",
        "Vanuatu",
        "Wallis and Futuna",
        "Samoa",
        "Yemen",
        "South Africa",
        "Zambia",
        "Zimbabwe"
    };

}
