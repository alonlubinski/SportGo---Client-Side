package com.alon.client.utils;

import com.alon.client.utils.elementUtils.FacilityStatus;
import com.alon.client.utils.elementUtils.FacilityType;
import com.alon.client.utils.elementUtils.MuscaleGroup;
import com.alon.client.utils.userUtils.UserRole;

public class Converter {

    public Converter() {
    }

    // Method that convert that user role to string.
    public static String convertUserRole(UserRole userRole){
        String str = null;
        switch(userRole.toString()){
            case "PLAYER":
                str = "Player";
                break;

            case "MANAGER":
                str = "Manager";
                break;

            case "ADMIN":
                str = "Admin";
                break;
        }
        return str;
    }

    // Method that convert the facility status to string.
    public static String convertFacilityStatus(FacilityStatus facilityStatus){
        String str = null;
        switch (facilityStatus.toString()){
            case "free":
                str = "Free";
                break;

            case "occupied":
                str = "Occupied";
                break;

            case "broken":
                str = "Broken";
                break;
        }
        return str;
    }

    // Method that convert that facility type to string.
    public static String convertFacilityType(FacilityType facilityType){
        String str = null;
        switch (facilityType.toString()){
            case "leg_press":
                str = "Leg Press";
                break;

            case "shoulder_press":
                str = "Shoulder Press";
                break;

            case "recumbent_bike":
                str = "Recumbent Bike";
                break;

            case "rowing_machine":
                str = "Rowing Machine";
                break;

            case "situp_bench":
                str = "Situp Bench";
                break;

            case "reverse_butterfly":
                str = "Reverse Butterfly";
                break;

            case "air_walker":
                str = "Air Walker";
                break;
        }
        return str;
    }

    // Method that convert the muscale group to string.
    public static String convertMuscaleGroup(MuscaleGroup muscaleGroup){
        String str = null;
        switch (muscaleGroup.toString()){
            case "hands":
                str = "Hands";
                break;

            case "legs":
                str = "Legs";
                break;

            case "upper_abdomen":
                str = "Upper Abdomen";
                break;

            case "lower_abdomen":
                str = "Lower Abdomen";
                break;

            case "upper_chest":
                str = "Upper Chest";
                break;

            case "lower_chest":
                str = "Lower Chest";
                break;

        }
        return str;
    }
}
