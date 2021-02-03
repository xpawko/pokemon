package pablo.demo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.*;


@Controller
@RequestMapping("/")
public class PokemonHandler {


    List<String> currentPokemonData;

    //    @Autowired
    PokemonHandler() {
        currentPokemonData = new ArrayList<>();

    }


    public float ratio(String attack, String defend) throws JSONException {


        RestTemplate restTemplate = new RestTemplate();
        String type = restTemplate.getForObject("https://pokeapi.co/api/v2/type/" + attack, String.class);

        JSONObject obj = new JSONObject(type);
        JSONObject x = obj.getJSONObject("damage_relations");


        Map<String, Object> newMap = new HashMap<>();
        for (int i = 0; i < x.names().length(); i++) {
            newMap.put((String) x.names().get(i), x.get((String) x.names().get(i)));

            JSONArray test = (JSONArray) x.get((String) x.names().get(i));
            if (test.toString().contains(defend) && ((String) x.names().get(i)).contains("from")) {

                return readRatio((String) x.names().get(i));

            }
        }
//
        return 1;
//    }
//
    }

    private float readRatio(String ratio) {
        switch (ratio) {
            case "no_damage_from":
                return 0;
            case "half_damage_from":
                return 0.5F;
//            case "no_damage_to":
//                return 2;
//            case "half_damage_to":
//                return 2;
//            case "double_damage_to":
//                return 2;
            case "double_damage_from":
                return 2;


        }
        return 1;
    }


    float getFinalRatio(String request) {

        List<String> defender = Arrays.asList((Arrays.asList((request.split("-> ")))).get(0).toString().replaceAll("\\[|\\]", "").split(" "));
        List<String> attacker = Arrays.asList((Arrays.asList((request.split("-> ")))).get(1).toString().replaceAll("\\[|\\]", "").split(" "));


        float multiplyattack = 1;
        for (String attack : attacker) {
            for (String defend : defender) {
                try {
                    multiplyattack *= ratio(attack, defend);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return multiplyattack;
    }


    @PostMapping("/addtype")
    public String addConf(Model model, @RequestParam String type) {


        if (!type.matches("(([a-z]+ [a-z]+)||([a-z]+)) -> (([a-z]+ [a-z]+)||([a-z]+))")) {
            model.addAttribute("message", "wrong syntax, use exact synax as example");
            var ratios = currentPokemonData;
            model.addAttribute("type", type);
            model.addAttribute("ratios", ratios);
            return "pokemon";
        }

        System.out.println(type + " to jes typ");
        float finalRatio = getFinalRatio(type);

        currentPokemonData.add(type + " -> " + finalRatio);


        var ratios = currentPokemonData;
        model.addAttribute("type", type);
        model.addAttribute("ratios", ratios);

        return "pokemon";
    }


    @GetMapping("/pokemon")
    public String viewData(Model model) {


        var ratios = currentPokemonData;

        var type = "";

        model.addAttribute("type", type);
        model.addAttribute("ratios", ratios);


        return "pokemon";


    }
}
