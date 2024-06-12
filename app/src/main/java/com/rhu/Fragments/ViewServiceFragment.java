package com.rhu.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rhu.R;
import com.rhu.Utils.Utils;

public class ViewServiceFragment extends Fragment {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    View view;
    RoundedImageView imgServiceBanner;
    TextView tvServiceName, tvServiceDetails;
    MaterialButton btnBookAnAppointment;
    String service, serviceCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_service, container, false);

        initializeFirebase();
        initializeViews();
        loadSelectedService();
        handleUserInteraction();

        return view;
    }

    private void handleUserInteraction() {
        btnBookAnAppointment.setOnClickListener(view -> {
            if (USER != null) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment formAppointmentFragment = new FormAppointmentFragment();

                Bundle args = new Bundle();
                switch (service) {
                    case "Medical Consultation":
                        serviceCode = "Consultation";
                        break;
                    case "Family Planning and Well-Being":
                        serviceCode = "Family Planning";
                        break;
                    case "Radiology Imaging (X-Ray)":
                        serviceCode = "X-Ray";
                        break;
                    case "Labor and Delivery of Normal Pregnancy":
                        serviceCode = "Labor and Delivery";
                        break;
                    case "TB-DOTS Treatment Program":
                        serviceCode = "TB-DOTS";
                        break;
                    case "Circumcision (Tuli)":
                        serviceCode = "Circumcision";
                        break;
                    case "Medical Certificate Issuance":
                        serviceCode = "Medical Certificate";
                        break;
                    case "Micronutrient Supplementation":
                        serviceCode = "Micronutrient Supplementation";
                        break;
                    case "Vaccination":
                        serviceCode = "Vaccination";
                        break;
                    case "Wound Care":
                        serviceCode = "Wound Care";
                        break;
                    case "Sputum Test":
                        serviceCode = "Sputum Test";
                        break;
                    case "Laboratory Testing":
                        serviceCode = "Laboratory";
                        break;
                    case "Hemoglobin Profiling":
                        serviceCode = "Hemoglobin";
                        break;
                    case "Comprehensive Urine Analysis":
                        serviceCode = "Urinalysis";
                        break;
                    case "Glucose Fasting/Blood Sugar Analysis":
                        serviceCode = "Fasting Blood Sugar";
                        break;
                    case "Hepatitis B Blood Test":
                        serviceCode = "Hepatitis B Blood Test";
                        break;
                    case "HIV Screening":
                        serviceCode = "HIV Screening";
                        break;
                    case "Newborn Care":
                        serviceCode = "Newborn Care";
                        break;
                    case "Newborn Screening":
                        serviceCode = "Newborn Screening";
                        break;
                };
                args.putString("selected_service", serviceCode);
                formAppointmentFragment.setArguments(args);

                fragmentTransaction.replace(R.id.fragmentHolder, formAppointmentFragment, "APPOINTMENT_FORM_FRAGMENT");
                fragmentTransaction.addToBackStack("APPOINTMENT_FORM_FRAGMENT");
                fragmentTransaction.commit();
            }
            else {
                BottomNavigationView bottom_navbar = requireActivity().findViewById(R.id.bottom_navbar);

                Utils.newPatientDialog(requireActivity());
                bottom_navbar.getMenu().getItem(0).setChecked(true);
            }
        });
    }

    private void initializeViews() {
        btnBookAnAppointment = view.findViewById(R.id.btnBookAnAppointment);
        imgServiceBanner = view.findViewById(R.id.imgServiceBanner);
        tvServiceName = view.findViewById(R.id.tvServiceName);
        tvServiceDetails = view.findViewById(R.id.tvServiceDetails);

        service = requireArguments().getString("service");
    }

    private void loadSelectedService() {
        tvServiceName.setText(service);

        if (service.equals("Medical Consultation")) {
            imgServiceBanner.setImageResource(R.drawable.banner_consultation);
            tvServiceDetails.setText("Welcome to our personalized consultation services, where our dedicated healthcare professionals are committed to providing you with the highest standard of care. During your consultation, our team takes the time to delve into your medical history, ensuring a thorough understanding of your health journey. We encourage open communication, allowing you to express your concerns, ask questions, and actively participate in decisions about your healthcare. Our goal is not only to address immediate health issues but to establish a lasting partnership for your overall well-being. Whether you're seeking routine check-ups or dealing with specific health concerns, our experts are here to offer guidance, support, and tailored advice to empower you on your path to optimal health.");
        }
        else if (service.equals("Family Planning and Well-Being")) {
            imgServiceBanner.setImageResource(R.drawable.banner_family_planning);
            tvServiceDetails.setText("Empower yourself with knowledge and choices through our family planning services. Our team of experts understands that reproductive health is a deeply personal journey, and we are here to provide the support and guidance you need. From contraceptive options to fertility planning, our services are designed to cater to your individual needs and aspirations. We believe in fostering a positive and empowering approach to family planning, where individuals and families can make informed decisions that align with their health goals. Join us on a journey where your reproductive well-being takes center stage, and your choices are respected and supported every step of the way.");
        }
        else if (service.equals("Radiology Imaging (X-Ray)")) {
            imgServiceBanner.setImageResource(R.drawable.banner_xray);
            tvServiceDetails.setText("Embark on a journey into the world of advanced medical imaging with our X-ray services. Our facility is equipped with state-of-the-art technology that goes beyond routine diagnostics, providing detailed and intricate images of internal structures. These images serve as invaluable tools for healthcare professionals in the diagnosis and monitoring of various medical conditions. From identifying fractures to detecting underlying issues, our X-ray services play a crucial role in guiding treatment plans and ensuring the most effective care. We understand the significance of each image and its impact on your health journey, and our team is dedicated to providing a non-invasive and informative experience. Trust us to deliver clarity and precision through our X-ray services, supporting you on the path to optimal health.");
        }
        else if (service.equals("Labor and Delivery of Normal Pregnancy")) {
            imgServiceBanner.setImageResource(R.drawable.banner_birthing);
            tvServiceDetails.setText("Experience the warmth and safety of our Labor and Delivery of Normal Pregnancy services, where the journey to motherhood is approached with care and dedication. Our trained professionals understand the significance of this transformative experience and are committed to ensuring a comfortable and supportive environment for expectant mothers. From prenatal care to the birthing process and postnatal support, our services encompass a comprehensive approach to maternal well-being. Trust us to prioritize your health and the health of your child, providing a range of services that facilitate a positive birthing experience.");
        }
        else if (service.equals("TB-DOTS Treatment Program")) {
            imgServiceBanner.setImageResource(R.drawable.banner_tbdots);
            tvServiceDetails.setText("Navigating the path to recovery from tuberculosis requires more than just medication—it demands structured and supervised care. Welcome to our TB-DOTS program, where our healthcare professionals are committed to guiding you through a treatment plan that ensures adherence and promotes effective recovery. Tuberculosis is a complex condition, and our program goes beyond the prescription, providing a supportive and monitored environment. We understand the importance of preventing drug resistance and minimizing potential side effects. Our team is dedicated to not only treating tuberculosis but also ensuring that your health and well-being are at the forefront of the entire treatment journey.");
        }
        else if (service.equals("Circumcision (Tuli)")) {
            imgServiceBanner.setImageResource(R.drawable.banner_circumcision);
            tvServiceDetails.setText("Trust our experienced healthcare professionals for safe and hygienic circumcision services. Whether for cultural or medical reasons, our services prioritize your well-being and comfort throughout the process. We understand the significance of this decision, and our team is committed to providing a supportive and professional environment. From pre-procedure consultations to post-operative care, our comprehensive circumcision services ensure that your health is at the forefront of every step. Join us in a journey where your comfort, safety, and overall well-being are paramount.");
        }
        else if (service.equals("Medical Certificate Issuance")) {
            imgServiceBanner.setImageResource(R.drawable.banner_medical_certificate);
            tvServiceDetails.setText("Obtaining official documentation for various purposes is made simple with our medical certificate services. Whether you require certification for sick leave, fitness assessments, or other health-related matters, our team ensures accuracy and reliability in every document. We understand that official documentation plays a crucial role in various aspects of life, and our services are designed to provide you with the necessary paperwork efficiently and professionally. Trust us to deliver the medical certificates you need, backed by a commitment to precision and attention to detail.");
        }
        else if (service.equals("Micronutrient Supplementation")) {
            imgServiceBanner.setImageResource(R.drawable.banner_supplementation);
            tvServiceDetails.setText("Elevate your well-being with our Micronutrient Supplementation service. Tailored for individual health needs, this offering ensures your body receives the vital vitamins and minerals essential for optimal functioning. From fortifying the immune system to supporting growth and development, our expert team provides personalized supplementation plans to address specific health goals. Embrace a healthier life by optimizing your micronutrient intake with our dedicated service");
        }
        else if (service.equals("Vaccination")) {
            imgServiceBanner.setImageResource(R.drawable.banner_immunization);
            tvServiceDetails.setText("Safeguard yourself and your loved ones against preventable diseases with our immunization services. Our scheduled vaccination programs cover every stage of life, providing comprehensive protection against a range of illnesses. By choosing our immunization services, you contribute to community-wide health, fostering a safer and healthier environment for everyone. We understand the importance of vaccines in preventing the spread of infectious diseases, and our commitment is to provide you with the necessary information and support to make informed decisions about your immunization journey.");
        }
        else if (service.equals("Wound Care")) {
            imgServiceBanner.setImageResource(R.drawable.banner_wound_care);
            tvServiceDetails.setText("Experience compassionate and expert care with our Wound Care service. Our skilled healthcare professionals specialize in the meticulous assessment and treatment of wounds, prioritizing your comfort and swift recovery. Whether managing surgical wounds, injuries, or chronic conditions, our team employs advanced techniques and evidence-based practices to promote optimal healing. From wound cleaning and dressing changes to infection prevention, we are committed to providing comprehensive care tailored to your unique needs. Trust us to guide you through the healing process, ensuring that every step is taken to promote the best possible outcome for your well-being.");
        }
        else if (service.equals("Sputum Test")) {
            imgServiceBanner.setImageResource(R.drawable.banner_sputum_test);
            tvServiceDetails.setText("Discover precise diagnostic insights with our Sputum Test service. Designed for accuracy and efficiency, this diagnostic offering focuses on the analysis of sputum, a key respiratory sample. Our experienced healthcare team ensures a seamless and comfortable testing experience, guiding you through the process to obtain reliable results. Whether screening for respiratory infections, assessing lung health, or identifying specific pathogens, our Sputum Test is a vital tool in understanding your respiratory well-being. Trust in our commitment to delivering timely and accurate results, empowering you with valuable information for informed healthcare decisions.");
        }
        else if (service.equals("Hemoglobin Profiling")) {
            imgServiceBanner.setImageResource(R.drawable.banner_hemoglobin);
            tvServiceDetails.setText("Take control of your blood health with our specialized hemoglobin testing services. Beyond a simple blood test, our comprehensive approach delves into the intricacies of your body's oxygen-carrying capacity. Hemoglobin testing is a vital tool in the early detection of conditions such as anemia, allowing for proactive management and personalized care plans. Our experienced healthcare professionals ensure that your hemoglobin test is not just a number but a detailed assessment of your overall blood health. Through accurate measurements and a thorough understanding of your unique health profile, we empower you to make informed decisions about your well-being, fostering a proactive approach to optimal health.");
        }
        else if (service.equals("Comprehensive Urine Analysis")) {
            imgServiceBanner.setImageResource(R.drawable.banner_urinalysis);
            tvServiceDetails.setText("Embark on a comprehensive assessment of your urinary system health with our urinalysis services. Beyond routine check-ups, our thorough examination of urine provides valuable insights into kidney function and can detect a range of health conditions. From identifying potential kidney issues to uncovering signs of other systemic concerns, our urinalysis services contribute to proactive healthcare. We understand that early detection is key, and our experienced team is dedicated to ensuring that your urinalysis is not just a test but a detailed exploration of your urinary system's well-being. Trust us for accurate assessments and personalized care plans that support you on your journey to optimal health.");
        }
        else if (service.equals("Glucose Fasting/Blood Sugar Analysis")) {
            imgServiceBanner.setImageResource(R.drawable.banner_fasting_blood_sugar);
            tvServiceDetails.setText("Managing diabetes requires a precise and proactive approach, and our Fasting Blood Sugar (FBS) testing services are designed to be a cornerstone of that strategy. Beyond routine monitoring, our FBS tests provide accurate measurements that are crucial in the effective control and treatment of diabetes. We understand the impact that diabetes can have on your life, and our commitment is to deliver results that empower you to take charge of your health. By providing timely and reliable information, our FBS testing services contribute to your overall diabetes management plan, allowing for informed decision-making and a proactive stance in the pursuit of optimal health.");
        }
        else if (service.equals("Hepatitis B Blood Test")) {
            imgServiceBanner.setImageResource(R.drawable.banner_hepatitis);
            tvServiceDetails.setText("Prioritize your liver health with our Hepatitis B Blood Test service. This essential screening is designed to detect the presence of the Hepatitis B virus, a potentially serious infection affecting the liver. Our proficient healthcare team ensures a simple and efficient blood test process, providing you with accurate results to assess your Hepatitis B status. Early detection is key to effective management, and our service empowers you with the information needed to make informed decisions about your health. Take a proactive step towards liver well-being with our Hepatitis B Blood Test, where precision and care converge to safeguard your health.");
        }
        else if (service.equals("HIV Screening")) {
            imgServiceBanner.setImageResource(R.drawable.banner_hiv);
            tvServiceDetails.setText("Take control of your health journey with our HIV Screening service. Our confidential and accessible screening process is designed to provide you with reliable results regarding your HIV status. Led by a compassionate healthcare team, we ensure a discreet and supportive environment for testing. Early detection is crucial for timely intervention, and our HIV Screening service is your first line of defense. Empower yourself with knowledge about your HIV status, promoting informed decisions and proactive health management. Trust us to prioritize your well-being through our commitment to accuracy, confidentiality, and a supportive testing experience.");
        }
        else if (service.equals("Newborn Care")) {
            imgServiceBanner.setImageResource(R.drawable.banner_newborn_care);
            tvServiceDetails.setText("Welcome to our Newborn Care service, where the arrival of your little one is met with expert guidance and dedicated support. Our experienced healthcare professionals are committed to ensuring a seamless transition for both you and your newborn. From essential health checkups to personalized care plans, we prioritize the well-being of your precious bundle of joy. Our comprehensive Newborn Care service covers everything from feeding and sleep routines to developmental milestones, creating a nurturing environment for your baby's growth. Trust us to be your partner in this incredible journey, providing the care and information you need to give your newborn the best start in life.");
        }
        else if (service.equals("Newborn Screening")) {
            imgServiceBanner.setImageResource(R.drawable.banner_newborn_screening);
            tvServiceDetails.setText("Introducing our Newborn Screening service, a dedicated initiative to safeguard the health and well-being of your newest family member. Designed to detect potential health issues early on, our screening covers a range of vital tests to ensure a comprehensive understanding of your baby's health. From metabolic disorders to hearing and vision assessments, we are committed to providing you with valuable insights into your newborn's overall well-being. Our skilled healthcare team combines expertise with care to conduct these screenings, offering you peace of mind and the assurance that your baby's health is in good hands. Embrace the journey of parenthood with confidence, knowing that our Newborn Screening service is here to support your baby's health from the very beginning.");
        }
    }
}