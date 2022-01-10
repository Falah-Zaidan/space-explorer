package com.example.spaceexplorer.util

class Constants {
    companion object {

        val nasa_api_key = "5vv4KVOuKmLQ852nlkS2WTpjofWpcjnAj4yj1NdO"

        var username: String? = null
        var authToken: String? = null

        val adminToken: String = "Token 4d0d38a8857e24501812f9eab292e08426366436"
        var proceedToken: String = "Token: 13895be00bb9df49fc8aa6169b9b47eca54bd24e"

        var initialSelectedRover = "Spirit"

        var apod_start_bound = "1995-06-16"

        val spirit_start_bound = "2004-02-13"
        val opportunity_start_bound = "2004-01-26"
        val curiosity_start_bound = "2015-12-02"

        val spirit_end_bound = "2010-03-06"
        val opportunity_end_bound = "2018-06-09"
        val curiosity_end_bound = "2021-09-01"

        val curiosty_text = "Curiosity"
        val opportunity_text = "Opportunity"
        val spirit_text = "Spirit"

        val curiosity_initial_date = "2015-12-02"
        val spirit_initial_date = "2004-02-13"
        val opporunity_initial_date = "2004-01-26"

        val storagePermissionRequestCode = 1

        val date_picker_text = "Select date"

        // ==== random values ====
        val no_explanation = "no explanation"
        val abc_rover_name = "abc"

        fun setLoggedInUser(mUsername: String) {
            username = mUsername
        }

        fun getCurrentLoggedInUsername(): String? {

            if (username != null) {
                return username
            } else {
                return "testUser" //for testing
            }
        }

        fun getCurrentLoggedinProfilePicture(): String {
            return "https://picsum.photos/200/300"
        }

    }
}
