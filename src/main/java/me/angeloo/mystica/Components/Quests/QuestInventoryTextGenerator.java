package me.angeloo.mystica.Components.Quests;

import java.util.Map;

public class QuestInventoryTextGenerator {

    private final Map<Character, String> characterStringMapLine0 = Map.<Character, String>ofEntries(
            Map.entry(' ', " "), Map.entry('!',"\uE306"), Map.entry('"',"\uE307"), Map.entry('#',"\uE308"),
            Map.entry('$', "\uE309"), Map.entry('%', "\uE30A"), Map.entry('&', "\uE30B"), Map.entry('\'', "\uE30C"),
            Map.entry('(', "\uE30D"), Map.entry(')', "\uE30E"), Map.entry('*', "\uE30F"), Map.entry('+', "\uE310"),
            Map.entry(',', "\uE311"), Map.entry('-', "\uE312"), Map.entry('.', "\uE313"), Map.entry('/', "\uE314"),
            Map.entry('0', "\uE315"), Map.entry('1', "\uE316"), Map.entry('2', "\uE317"), Map.entry('3', "\uE318"),
            Map.entry('4', "\uE319"), Map.entry('5', "\uE31A"), Map.entry('6', "\uE31B"), Map.entry('7', "\uE31C"),
            Map.entry('8', "\uE31D"), Map.entry('9', "\uE31E"), Map.entry(':', "\uE31F"), Map.entry(';', "\uE320"),
            Map.entry('<', "\uE321"), Map.entry('=', "\uE322"), Map.entry('>', "\uE323"), Map.entry('?', "\uE324"),
            Map.entry('@', "\uE325"), Map.entry('A', "\uE326"), Map.entry('B', "\uE327"), Map.entry('C', "\uE328"),
            Map.entry('D', "\uE329"), Map.entry('E', "\uE32A"), Map.entry('F', "\uE32B"), Map.entry('G', "\uE32C"),
            Map.entry('H', "\uE32D"), Map.entry('I', "\uE32E"), Map.entry('J', "\uE32F"), Map.entry('K', "\uE330"),
            Map.entry('L', "\uE331"), Map.entry('M', "\uE332"), Map.entry('N', "\uE333"), Map.entry('O', "\uE334"),
            Map.entry('P', "\uE335"), Map.entry('Q', "\uE336"), Map.entry('R', "\uE337"), Map.entry('S', "\uE338"),
            Map.entry('T', "\uE339"), Map.entry('U', "\uE33A"), Map.entry('V', "\uE33B"), Map.entry('W', "\uE33C"),
            Map.entry('X', "\uE33D"), Map.entry('Y', "\uE33E"), Map.entry('Z', "\uE33F"), Map.entry('[', "\uE340"),
            Map.entry('\\', "\uE341"), Map.entry(']', "\uE342"), Map.entry('^', "\uE343"), Map.entry('_', "\uE344"),
            Map.entry('`', "\uE345"), Map.entry('a', "\uE346"), Map.entry('b', "\uE347"), Map.entry('c', "\uE348"),
            Map.entry('d', "\uE349"), Map.entry('e', "\uE34A"), Map.entry('f', "\uE34B"), Map.entry('g', "\uE34C"),
            Map.entry('h', "\uE34D"), Map.entry('i', "\uE34E"), Map.entry('j', "\uE34F"), Map.entry('k', "\uE350"),
            Map.entry('l', "\uE351"), Map.entry('m', "\uE352"), Map.entry('n', "\uE353"), Map.entry('o', "\uE354"),
            Map.entry('p', "\uE355"), Map.entry('q', "\uE356"), Map.entry('r', "\uE357"), Map.entry('s', "\uE358"),
            Map.entry('t', "\uE359"), Map.entry('u', "\uE35A"), Map.entry('v', "\uE35B"), Map.entry('w', "\uE35C"),
            Map.entry('x', "\uE35D"), Map.entry('y', "\uE35E"), Map.entry('z', "\uE35F"), Map.entry('{', "\uE360"),
            Map.entry('|', "\uE361"), Map.entry('}', "\uE362"), Map.entry('~', "\uE363")
    );

    public QuestInventoryTextGenerator(){

    }

    public String getInventoryText(String[] text){

        StringBuilder inventoryText = new StringBuilder();

        int index = 0;
        for(String s : text){

            //check for unicodes of negative space
            for(char c : s.toCharArray()){
                inventoryText.append(getCharacter(c, index));
            }

            index ++;
        }

        return inventoryText.toString();
    }

    private String getCharacter(char c, int line){

        //depend on index

        return characterStringMapLine0.getOrDefault(c,"");

    }

}
