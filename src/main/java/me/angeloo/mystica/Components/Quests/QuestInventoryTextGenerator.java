package me.angeloo.mystica.Components.Quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestInventoryTextGenerator {

    private final List<Map<Character,String>> characterStringMapList = new ArrayList<>();


    public QuestInventoryTextGenerator(){
        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE306"), Map.entry('"', "\uE307"), Map.entry('#', "\uE308"),
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
                Map.entry('|', "\uE361"), Map.entry('}', "\uE362"), Map.entry('~', "\uE363")));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE364"), Map.entry('"', "\uE365"), Map.entry('#', "\uE366"),
                Map.entry('$', "\uE367"), Map.entry('%', "\uE368"), Map.entry('&', "\uE369"), Map.entry('\'', "\uE36A"),
                Map.entry('(', "\uE36B"), Map.entry(')', "\uE36C"), Map.entry('*', "\uE36D"), Map.entry('+', "\uE36E"),
                Map.entry(',', "\uE36F"), Map.entry('-', "\uE370"), Map.entry('.', "\uE371"), Map.entry('/', "\uE372"),
                Map.entry('0', "\uE373"), Map.entry('1', "\uE374"), Map.entry('2', "\uE375"), Map.entry('3', "\uE376"),
                Map.entry('4', "\uE377"), Map.entry('5', "\uE378"), Map.entry('6', "\uE379"), Map.entry('7', "\uE37A"),
                Map.entry('8', "\uE37B"), Map.entry('9', "\uE37C"), Map.entry(':', "\uE37D"), Map.entry(';', "\uE37E"),
                Map.entry('<', "\uE37F"), Map.entry('=', "\uE380"), Map.entry('>', "\uE381"), Map.entry('?', "\uE382"),
                Map.entry('@', "\uE383"), Map.entry('A', "\uE384"), Map.entry('B', "\uE385"), Map.entry('C', "\uE386"),
                Map.entry('D', "\uE387"), Map.entry('E', "\uE388"), Map.entry('F', "\uE389"), Map.entry('G', "\uE38A"),
                Map.entry('H', "\uE38B"), Map.entry('I', "\uE38C"), Map.entry('J', "\uE38D"), Map.entry('K', "\uE38E"),
                Map.entry('L', "\uE38F"), Map.entry('M', "\uE390"), Map.entry('N', "\uE391"), Map.entry('O', "\uE392"),
                Map.entry('P', "\uE393"), Map.entry('Q', "\uE394"), Map.entry('R', "\uE395"), Map.entry('S', "\uE396"),
                Map.entry('T', "\uE397"), Map.entry('U', "\uE398"), Map.entry('V', "\uE399"), Map.entry('W', "\uE39A"),
                Map.entry('X', "\uE39B"), Map.entry('Y', "\uE39C"), Map.entry('Z', "\uE39D"), Map.entry('[', "\uE39E"),
                Map.entry('\\', "\uE39F"), Map.entry(']', "\uE3F0"), Map.entry('^', "\uE3F1"), Map.entry('_', "\uE3F2"),
                Map.entry('`', "\uE3F3"), Map.entry('a', "\uE3F4"), Map.entry('b', "\uE3F5"), Map.entry('c', "\uE3F6"),
                Map.entry('d', "\uE3F7"), Map.entry('e', "\uE3F8"), Map.entry('f', "\uE3F9"), Map.entry('g', "\uE3FA"),
                Map.entry('h', "\uE3FB"), Map.entry('i', "\uE3FC"), Map.entry('j', "\uE3FD"), Map.entry('k', "\uE3FE"),
                Map.entry('l', "\uE3FF"), Map.entry('m', "\uE400"), Map.entry('n', "\uE401"), Map.entry('o', "\uE402"),
                Map.entry('p', "\uE403"), Map.entry('q', "\uE404"), Map.entry('r', "\uE405"), Map.entry('s', "\uE406"),
                Map.entry('t', "\uE407"), Map.entry('u', "\uE408"), Map.entry('v', "\uE409"), Map.entry('w', "\uE40A"),
                Map.entry('x', "\uE40B"), Map.entry('y', "\uE40C"), Map.entry('z', "\uE40D"), Map.entry('{', "\uE40E"),
                Map.entry('|', "\uE40F"), Map.entry('}', "\uE410"), Map.entry('~', "\uE411")));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE412"), Map.entry('"', "\uE413"), Map.entry('#', "\uE414"),
                Map.entry('$', "\uE415"), Map.entry('%', "\uE416"), Map.entry('&', "\uE417"), Map.entry('\'', "\uE418"),
                Map.entry('(', "\uE419"), Map.entry(')', "\uE41A"), Map.entry('*', "\uE41B"), Map.entry('+', "\uE41C"),
                Map.entry(',', "\uE41D"), Map.entry('-', "\uE41E"), Map.entry('.', "\uE41F"), Map.entry('/', "\uE420"),
                Map.entry('0', "\uE421"), Map.entry('1', "\uE422"), Map.entry('2', "\uE423"), Map.entry('3', "\uE424"),
                Map.entry('4', "\uE425"), Map.entry('5', "\uE426"), Map.entry('6', "\uE427"), Map.entry('7', "\uE428"),
                Map.entry('8', "\uE429"), Map.entry('9', "\uE42A"), Map.entry(':', "\uE42B"), Map.entry(';', "\uE42C"),
                Map.entry('<', "\uE42D"), Map.entry('=', "\uE42E"), Map.entry('>', "\uE42F"), Map.entry('?', "\uE430"),
                Map.entry('@', "\uE431"), Map.entry('A', "\uE432"), Map.entry('B', "\uE433"), Map.entry('C', "\uE434"),
                Map.entry('D', "\uE435"), Map.entry('E', "\uE436"), Map.entry('F', "\uE437"), Map.entry('G', "\uE438"),
                Map.entry('H', "\uE439"), Map.entry('I', "\uE43A"), Map.entry('J', "\uE43B"), Map.entry('K', "\uE43C"),
                Map.entry('L', "\uE43D"), Map.entry('M', "\uE43E"), Map.entry('N', "\uE43F"), Map.entry('O', "\uE440"),
                Map.entry('P', "\uE441"), Map.entry('Q', "\uE442"), Map.entry('R', "\uE443"), Map.entry('S', "\uE444"),
                Map.entry('T', "\uE445"), Map.entry('U', "\uE446"), Map.entry('V', "\uE447"), Map.entry('W', "\uE448"),
                Map.entry('X', "\uE449"), Map.entry('Y', "\uE44A"), Map.entry('Z', "\uE44B"), Map.entry('[', "\uE44C"),
                Map.entry('\\', "\uE44D"), Map.entry(']', "\uE44E"), Map.entry('^', "\uE44F"), Map.entry('_', "\uE450"),
                Map.entry('`', "\uE451"), Map.entry('a', "\uE452"), Map.entry('b', "\uE453"), Map.entry('c', "\uE454"),
                Map.entry('d', "\uE455"), Map.entry('e', "\uE456"), Map.entry('f', "\uE457"), Map.entry('g', "\uE458"),
                Map.entry('h', "\uE459"), Map.entry('i', "\uE45A"), Map.entry('j', "\uE45B"), Map.entry('k', "\uE45C"),
                Map.entry('l', "\uE45D"), Map.entry('m', "\uE45E"), Map.entry('n', "\uE45F"), Map.entry('o', "\uE460"),
                Map.entry('p', "\uE461"), Map.entry('q', "\uE462"), Map.entry('r', "\uE463"), Map.entry('s', "\uE464"),
                Map.entry('t', "\uE465"), Map.entry('u', "\uE466"), Map.entry('v', "\uE467"), Map.entry('w', "\uE468"),
                Map.entry('x', "\uE469"), Map.entry('y', "\uE46A"), Map.entry('z', "\uE46B"), Map.entry('{', "\uE46C"),
                Map.entry('|', "\uE46D"), Map.entry('}', "\uE46E"), Map.entry('~', "\uE46F")
        ));

    }

    public String getInventoryText(List<String> text){

        StringBuilder inventoryText = new StringBuilder();

        int index = 0;
        for(String s : text){

            for(char c : s.toCharArray()){

                inventoryText.append(getCharacter(c, index));
            }

            index ++;
        }

        return inventoryText.toString();
    }

    private String getCharacter(char c, int line){

        if(line > characterStringMapList.size()-1){

            return "";
        }

        return characterStringMapList.get(line).getOrDefault(c, String.valueOf(c));
    }

}
