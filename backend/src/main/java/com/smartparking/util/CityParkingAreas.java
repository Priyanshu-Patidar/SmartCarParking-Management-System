package com.smartparking.util;

import java.util.List;
import java.util.Map;

/**
 * Real area coordinates per city — each parking is spread across different neighborhoods.
 */
public final class CityParkingAreas {

    public record Area(String locality, String address, double latitude, double longitude) {}

    public static final String[] LOCATION_SUFFIXES = {
            "Central Plaza Parking", "Metro Station Parking", "Mall Parking Hub", "Airport Parking Zone",
            "Tech Park Garage", "Railway Station Parking", "Stadium Parking", "Hospital Parking",
            "University Parking", "Market Street Parking", "Green EV Hub"
    };

    private static final Map<String, List<Area>> AREAS = Map.ofEntries(
            Map.entry("Mumbai", List.of(
                    new Area("Colaba", "Colaba Causeway, Mumbai", 18.9067, 72.8147),
                    new Area("Andheri", "Andheri West, Mumbai", 19.1197, 72.8468),
                    new Area("Bandra", "Bandra Kurla Complex, Mumbai", 19.0596, 72.8295),
                    new Area("Powai", "Hiranandani Gardens, Powai", 19.1176, 72.9060),
                    new Area("Goregaon", "Western Express Highway, Goregaon", 19.1663, 72.8526),
                    new Area("Dadar", "Dadar TT, Mumbai", 19.0178, 72.8478),
                    new Area("Worli", "Worli Sea Face, Mumbai", 19.0176, 72.8133),
                    new Area("Juhu", "Juhu Tara Road, Mumbai", 19.1075, 72.8263),
                    new Area("Thane", "Ghodbunder Road, Thane", 19.2183, 72.9781),
                    new Area("Navi Mumbai", "Vashi Sector 17, Navi Mumbai", 19.0738, 72.9986),
                    new Area("Malad", "Malad West, Mumbai", 19.1860, 72.8489)
            )),
            Map.entry("Bangalore", List.of(
                    new Area("MG Road", "MG Road, Bangalore", 12.9756, 77.6064),
                    new Area("Indiranagar", "100 Feet Road, Indiranagar", 12.9784, 77.6408),
                    new Area("Whitefield", "ITPL Main Road, Whitefield", 12.9698, 77.7500),
                    new Area("Electronic City", "Electronic City Phase 1", 12.8456, 77.6603),
                    new Area("Koramangala", "5th Block, Koramangala", 12.9352, 77.6245),
                    new Area("Hebbal", "Hebbal Flyover, Bangalore", 13.0358, 77.5970),
                    new Area("Jayanagar", "4th Block, Jayanagar", 12.9308, 77.5838),
                    new Area("Marathahalli", "Marathahalli Bridge", 12.9591, 77.6974),
                    new Area("Yelahanka", "Yelahanka New Town", 13.1007, 77.5963),
                    new Area("BTM Layout", "BTM 2nd Stage", 12.9166, 77.6101),
                    new Area("Banashankari", "Banashankari Stage 3", 12.9255, 77.5468)
            )),
            Map.entry("Delhi", List.of(
                    new Area("Connaught Place", "Connaught Place, New Delhi", 28.6315, 77.2167),
                    new Area("Karol Bagh", "Ajmal Khan Road, Karol Bagh", 28.6519, 77.1909),
                    new Area("Dwarka", "Sector 21, Dwarka", 28.5921, 77.0460),
                    new Area("Saket", "Select Citywalk, Saket", 28.5245, 77.2066),
                    new Area("Aerocity", "Aerocity, Mahipalpur", 28.5524, 77.0880),
                    new Area("Rohini", "Sector 10, Rohini", 28.7495, 77.0565),
                    new Area("Lajpat Nagar", "Central Market, Lajpat Nagar", 28.5677, 77.2433),
                    new Area("Nehru Place", "Nehru Place Metro", 28.5494, 77.2510),
                    new Area("Chandni Chowk", "Chandni Chowk, Old Delhi", 28.6562, 77.2410),
                    new Area("Hauz Khas", "Hauz Khas Village", 28.5494, 77.2001),
                    new Area("India Gate", "Rajpath, India Gate", 28.6129, 77.2295)
            )),
            Map.entry("Pune", List.of(
                    new Area("Shivajinagar", "FC Road, Shivajinagar", 18.5304, 73.8478),
                    new Area("Hinjewadi", "Phase 1, Hinjewadi", 18.5912, 73.7389),
                    new Area("Koregaon Park", "North Main Road, KP", 18.5362, 73.8939),
                    new Area("Kothrud", "Paud Road, Kothrud", 18.5074, 73.8077),
                    new Area("Viman Nagar", "Nagar Road, Viman Nagar", 18.5679, 73.9143),
                    new Area("Baner", "Baner Road, Pune", 18.5590, 73.7868),
                    new Area("Camp", "MG Road Camp Area", 18.5158, 73.8746),
                    new Area("Hadapsar", "Magarpatta City, Hadapsar", 18.5183, 73.9260),
                    new Area("Aundh", "D P Road, Aundh", 18.5590, 73.8077),
                    new Area("Wakad", "Mumbai Bangalore Highway, Wakad", 18.5991, 73.7622),
                    new Area("Pimpri", "Pimpri Chinchwad", 18.6298, 73.7997)
            )),
            Map.entry("Hyderabad", List.of(
                    new Area("Banjara Hills", "Road No 12, Banjara Hills", 17.4156, 78.4347),
                    new Area("Hitech City", "Madhapur, Hitech City", 17.4486, 78.3908),
                    new Area("Secunderabad", "Paradise Circle", 17.4399, 78.4983),
                    new Area("Gachibowli", "DLF Cyber City, Gachibowli", 17.4401, 78.3489),
                    new Area("Kukatpally", "KPHB Colony", 17.4849, 78.4138),
                    new Area("Charminar", "Laad Bazaar, Old City", 17.3616, 78.4747),
                    new Area("Begumpet", "Begumpet Airport Road", 17.4449, 78.4668),
                    new Area("Uppal", "Uppal Ring Road", 17.4014, 78.5582),
                    new Area("Miyapur", "Miyapur Metro", 17.4967, 78.3572),
                    new Area("Ameerpet", "Ameerpet X Roads", 17.4375, 78.4482),
                    new Area("LB Nagar", "LB Nagar Ring Road", 17.3661, 78.5470)
            )),
            Map.entry("Chennai", List.of(
                    new Area("T Nagar", "Usman Road, T Nagar", 13.0418, 80.2341),
                    new Area("Anna Nagar", "2nd Avenue, Anna Nagar", 13.0878, 80.2085),
                    new Area("Adyar", "Lattice Bridge Road, Adyar", 13.0067, 80.2574),
                    new Area("OMR", "Sholinganallur, OMR", 12.8996, 80.2209),
                    new Area("Velachery", "Velachery Main Road", 12.9815, 80.2180),
                    new Area("Porur", "Mount Poonamallee Road", 13.0358, 80.1520),
                    new Area("Tambaram", "GST Road, Tambaram", 12.9249, 80.1000),
                    new Area("Egmore", "Egmore Station Road", 13.0732, 80.2609),
                    new Area("Nungambakkam", "Nungambakkam High Road", 13.0569, 80.2425),
                    new Area("Chromepet", "Chromepet Station", 12.9516, 80.1390),
                    new Area("Guindy", "Race Course Road, Guindy", 13.0068, 80.2206)
            )),
            Map.entry("Kolkata", List.of(
                    new Area("Park Street", "Park Street, Kolkata", 22.5535, 88.3526),
                    new Area("Salt Lake", "Sector V, Salt Lake", 22.5726, 88.4339),
                    new Area("Howrah", "Howrah Station", 22.5958, 88.2636),
                    new Area("New Town", "Action Area 1, New Town", 22.5868, 88.4702),
                    new Area("Ballygunge", "Gariahat Road", 22.5234, 88.3665),
                    new Area("Dum Dum", "Dum Dum Airport Road", 22.6548, 88.4467),
                    new Area("Esplanade", "Esplanade Metro", 22.5644, 88.3519),
                    new Area("Behala", "Diamond Harbour Road", 22.5022, 88.3013),
                    new Area("Jadavpur", "Raja Subodh Mallick Road", 22.4987, 88.3719),
                    new Area("Sealdah", "Sealdah Station", 22.5678, 88.3703),
                    new Area("Rajarhat", "City Centre 2, Rajarhat", 22.6221, 88.4502)
            )),
            Map.entry("Ahmedabad", List.of(
                    new Area("Navrangpura", "CG Road, Navrangpura", 23.0360, 72.5611),
                    new Area("Satellite", "Satellite Road", 23.0225, 72.5176),
                    new Area("Vastrapur", "Vastrapur Lake", 23.0373, 72.5307),
                    new Area("Maninagar", "Maninagar Cross Road", 22.9987, 72.6012),
                    new Area("SG Highway", "Sola, SG Highway", 23.0709, 72.5170),
                    new Area("Bopal", "Bopal Ghuma Road", 23.0405, 72.4674),
                    new Area("Paldi", "Paldi Char Rasta", 23.0073, 72.5612),
                    new Area("Ghatlodia", "Ghatlodia", 23.0726, 72.5415),
                    new Area("Naroda", "Naroda Patiya", 23.0708, 72.6525),
                    new Area("Thaltej", "Thaltej", 23.0514, 72.5112),
                    new Area("Gandhinagar", "Infocity, Gandhinagar", 23.1985, 72.6468)
            )),
            Map.entry("Jaipur", List.of(
                    new Area("MI Road", "Mirza Ismail Road", 26.9157, 75.7873),
                    new Area("Malviya Nagar", "Malviya Nagar", 26.8547, 75.8242),
                    new Area("Vaishali Nagar", "Vaishali Nagar", 26.9124, 75.7437),
                    new Area("Raja Park", "Raja Park", 26.8957, 75.8280),
                    new Area("C Scheme", "C Scheme", 26.9038, 75.8042),
                    new Area("Mansarovar", "Mansarovar", 26.8573, 75.7662),
                    new Area("Tonk Road", "Tonk Road", 26.8381, 75.7952),
                    new Area("Jhotwara", "Jhotwara Industrial", 26.9417, 75.7565),
                    new Area("Sanganer", "Sanganer Airport", 26.8242, 75.8122),
                    new Area("Amer", "Amer Road", 26.9855, 75.8513),
                    new Area("Bani Park", "Bani Park", 26.9288, 75.7963)
            )),
            Map.entry("Lucknow", List.of(
                    new Area("Hazratganj", "Hazratganj", 26.8467, 80.9462),
                    new Area("Gomti Nagar", "Gomti Nagar", 26.8496, 81.0073),
                    new Area("Aliganj", "Aliganj", 26.8887, 80.9382),
                    new Area("Aminabad", "Aminabad", 26.8523, 80.9245),
                    new Area("Indira Nagar", "Indira Nagar", 26.8704, 80.9592),
                    new Area("Mahanagar", "Mahanagar", 26.8721, 80.9478),
                    new Area("Alambagh", "Alambagh", 26.7834, 80.9188),
                    new Area("Charbagh", "Charbagh Station", 26.8312, 80.9412),
                    new Area("Vrindavan Yojana", "Sultanpur Road", 26.7854, 80.9982),
                    new Area("Jankipuram", "Jankipuram", 26.9234, 80.9552),
                    new Area("Chowk", "Chowk Old City", 26.8728, 80.9064)
            )),
            Map.entry("Chandigarh", List.of(
                    new Area("Sector 17", "Sector 17", 30.7410, 76.7810),
                    new Area("Sector 22", "Sector 22", 30.7333, 76.7794),
                    new Area("Sector 35", "Sector 35", 30.7186, 76.7672),
                    new Area("Sector 43", "Sector 43", 30.7124, 76.7548),
                    new Area("Mohali", "Phase 7, Mohali", 30.7046, 76.7179),
                    new Area("Panchkula", "Sector 5, Panchkula", 30.6942, 76.8562),
                    new Area("Sector 8", "Sector 8", 30.7448, 76.7946),
                    new Area("Manimajra", "Manimajra", 30.7286, 76.8421),
                    new Area("Zirakpur", "Zirakpur", 30.6426, 76.8173),
                    new Area("IT Park", "Rajiv Gandhi IT Park", 30.7120, 76.8012),
                    new Area("Sector 34", "Sector 34", 30.7198, 76.7688)
            )),
            Map.entry("Kochi", List.of(
                    new Area("MG Road", "MG Road, Kochi", 9.9312, 76.2673),
                    new Area("Edappally", "Edappally", 10.0261, 76.3084),
                    new Area("Kakkanad", "Infopark, Kakkanad", 10.0159, 76.3419),
                    new Area("Fort Kochi", "Fort Kochi Beach", 9.9658, 76.2402),
                    new Area("Aluva", "Aluva", 10.1076, 76.3518),
                    new Area("Vyttila", "Vyttila Mobility Hub", 9.9674, 76.3185),
                    new Area("Palarivattom", "Palarivattom", 9.9974, 76.3088),
                    new Area("Maradu", "Maradu", 9.9426, 76.3182),
                    new Area("Thripunithura", "Thripunithura", 9.9477, 76.3492),
                    new Area("Kaloor", "Kaloor Stadium", 9.9943, 76.2881),
                    new Area("Willingdon Island", "Port Trust", 9.9471, 76.2592)
            )),
            Map.entry("Indore", List.of(
                    new Area("Vijay Nagar", "Vijay Nagar Square", 22.7248, 75.8889),
                    new Area("Palasia", "Palasia Square", 22.7244, 75.8839),
                    new Area("Bhawarkuan", "Bhawarkuan", 22.6847, 75.8742),
                    new Area("Rajwada", "Rajwada", 22.7196, 75.8577),
                    new Area("AB Road", "AB Road Dewas Naka", 22.7574, 75.8960),
                    new Area("Rau", "Rau", 22.6356, 75.7921),
                    new Area("Scheme 54", "Scheme 54", 22.7532, 75.8932),
                    new Area("MG Road", "MG Road Indore", 22.7192, 75.8712),
                    new Area("Lasudia", "Lasudia Mori", 22.8023, 75.9172),
                    new Area("Bengali Square", "Bengali Square", 22.7421, 75.8934),
                    new Area("Airport Road", "Ahilya Bai Holkar Airport", 22.7218, 75.8011)
            )),
            Map.entry("Nagpur", List.of(
                    new Area("Sitabuldi", "Sitabuldi Fort", 21.1458, 79.0882),
                    new Area("Dharampeth", "Dharampeth", 21.1384, 79.0602),
                    new Area("Sadar", "Sadar", 21.1645, 79.0856),
                    new Area("Civil Lines", "Civil Lines", 21.1497, 79.0807),
                    new Area("Wardha Road", "Wardha Road", 21.1176, 79.1012),
                    new Area("Koradi", "Koradi Road", 21.2387, 79.0921),
                    new Area("Hingna", "MIDC Hingna", 21.1143, 78.9812),
                    new Area("Manish Nagar", "Manish Nagar", 21.1076, 79.0487),
                    new Area("Trimurti Nagar", "Trimurti Nagar", 21.1178, 79.0512),
                    new Area("Khamla", "Khamla Square", 21.1265, 79.0789),
                    new Area("MIHAN", "MIHAN SEZ", 21.0821, 79.0854)
            )),
            Map.entry("Surat", List.of(
                    new Area("Adajan", "Adajan Patiya", 21.1950, 72.7937),
                    new Area("Vesu", "Vesu", 21.1418, 72.7811),
                    new Area("Varachha", "Varachha Road", 21.2143, 72.8545),
                    new Area("Katargam", "Katargam", 21.2147, 72.8234),
                    new Area("City Light", "City Light Road", 21.1702, 72.8311),
                    new Area("Udhna", "Udhna Darwaja", 21.1834, 72.8621),
                    new Area("Piplod", "Piplod", 21.1609, 72.7702),
                    new Area("Pal", "Pal", 21.1876, 72.7892),
                    new Area("Dumas", "Dumas Road", 21.1234, 72.7289),
                    new Area("Sarthana", "Sarthana", 21.2345, 72.9012),
                    new Area("Althan", "Althan", 21.1523, 72.8012)
            ))
    );

    private CityParkingAreas() {}

    public static List<Area> getAreas(String city) {
        return AREAS.getOrDefault(city, List.of(
                new Area("Central", city + " Central", 20.0, 77.0)
        ));
    }

    public static Area getArea(String city, int index) {
        List<Area> areas = getAreas(city);
        return areas.get(index % areas.size());
    }
}
