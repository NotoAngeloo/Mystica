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

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE470"), Map.entry('"', "\uE471"), Map.entry('#', "\uE472"),
                Map.entry('$', "\uE473"), Map.entry('%', "\uE474"), Map.entry('&', "\uE475"), Map.entry('\'', "\uE476"),
                Map.entry('(', "\uE477"), Map.entry(')', "\uE478"), Map.entry('*', "\uE479"), Map.entry('+', "\uE47A"),
                Map.entry(',', "\uE47B"), Map.entry('-', "\uE47C"), Map.entry('.', "\uE47D"), Map.entry('/', "\uE47E"),
                Map.entry('0', "\uE47F"), Map.entry('1', "\uE480"), Map.entry('2', "\uE481"), Map.entry('3', "\uE482"),
                Map.entry('4', "\uE483"), Map.entry('5', "\uE484"), Map.entry('6', "\uE485"), Map.entry('7', "\uE486"),
                Map.entry('8', "\uE487"), Map.entry('9', "\uE488"), Map.entry(':', "\uE489"), Map.entry(';', "\uE48A"),
                Map.entry('<', "\uE48B"), Map.entry('=', "\uE48C"), Map.entry('>', "\uE48D"), Map.entry('?', "\uE48E"),
                Map.entry('@', "\uE48F"), Map.entry('A', "\uE490"), Map.entry('B', "\uE491"), Map.entry('C', "\uE492"),
                Map.entry('D', "\uE493"), Map.entry('E', "\uE494"), Map.entry('F', "\uE495"), Map.entry('G', "\uE496"),
                Map.entry('H', "\uE497"), Map.entry('I', "\uE498"), Map.entry('J', "\uE499"), Map.entry('K', "\uE49A"),
                Map.entry('L', "\uE49B"), Map.entry('M', "\uE49C"), Map.entry('N', "\uE49D"), Map.entry('O', "\uE49E"),
                Map.entry('P', "\uE49F"), Map.entry('Q', "\uE4A0"), Map.entry('R', "\uE4A1"), Map.entry('S', "\uE4A2"),
                Map.entry('T', "\uE4A3"), Map.entry('U', "\uE4A4"), Map.entry('V', "\uE4A5"), Map.entry('W', "\uE4A6"),
                Map.entry('X', "\uE4A7"), Map.entry('Y', "\uE4A8"), Map.entry('Z', "\uE4A9"), Map.entry('[', "\uE4AA"),
                Map.entry('\\', "\uE4AB"), Map.entry(']', "\uE4AC"), Map.entry('^', "\uE4AD"), Map.entry('_', "\uE4AE"),
                Map.entry('`', "\uE4AF"), Map.entry('a', "\uE4B0"), Map.entry('b', "\uE4B1"), Map.entry('c', "\uE4B2"),
                Map.entry('d', "\uE4B3"), Map.entry('e', "\uE4B4"), Map.entry('f', "\uE4B5"), Map.entry('g', "\uE4B6"),
                Map.entry('h', "\uE4B7"), Map.entry('i', "\uE4B8"), Map.entry('j', "\uE4B9"), Map.entry('k', "\uE4BA"),
                Map.entry('l', "\uE4BB"), Map.entry('m', "\uE4BC"), Map.entry('n', "\uE4BD"), Map.entry('o', "\uE4BE"),
                Map.entry('p', "\uE4BF"), Map.entry('q', "\uE4C0"), Map.entry('r', "\uE4C1"), Map.entry('s', "\uE4C2"),
                Map.entry('t', "\uE4C3"), Map.entry('u', "\uE4C4"), Map.entry('v', "\uE4C5"), Map.entry('w', "\uE4C6"),
                Map.entry('x', "\uE4C7"), Map.entry('y', "\uE4C8"), Map.entry('z', "\uE4C9"), Map.entry('{', "\uE4CA"),
                Map.entry('|', "\uE4CB"), Map.entry('}', "\uE4CC"), Map.entry('~', "\uE4CD")
        ));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE4CE"), Map.entry('"', "\uE4CF"), Map.entry('#', "\uE4D0"),
                Map.entry('$', "\uE4D1"), Map.entry('%', "\uE4D2"), Map.entry('&', "\uE4D3"), Map.entry('\'', "\uE4D4"),
                Map.entry('(', "\uE4D5"), Map.entry(')', "\uE4D6"), Map.entry('*', "\uE4D7"), Map.entry('+', "\uE4D8"),
                Map.entry(',', "\uE4D9"), Map.entry('-', "\uE4DA"), Map.entry('.', "\uE4DB"), Map.entry('/', "\uE4DC"),
                Map.entry('0', "\uE4DD"), Map.entry('1', "\uE4DE"), Map.entry('2', "\uE4DF"), Map.entry('3', "\uE4E0"),
                Map.entry('4', "\uE4E1"), Map.entry('5', "\uE4E2"), Map.entry('6', "\uE4E3"), Map.entry('7', "\uE4E4"),
                Map.entry('8', "\uE4E5"), Map.entry('9', "\uE4E6"), Map.entry(':', "\uE4E7"), Map.entry(';', "\uE4E8"),
                Map.entry('<', "\uE4E9"), Map.entry('=', "\uE4EA"), Map.entry('>', "\uE4EB"), Map.entry('?', "\uE4EC"),
                Map.entry('@', "\uE4ED"), Map.entry('A', "\uE4EE"), Map.entry('B', "\uE4EF"), Map.entry('C', "\uE4F0"),
                Map.entry('D', "\uE4F1"), Map.entry('E', "\uE4F2"), Map.entry('F', "\uE4F3"), Map.entry('G', "\uE4F4"),
                Map.entry('H', "\uE4F5"), Map.entry('I', "\uE4F6"), Map.entry('J', "\uE4F7"), Map.entry('K', "\uE4F8"),
                Map.entry('L', "\uE4F9"), Map.entry('M', "\uE4FA"), Map.entry('N', "\uE4FB"), Map.entry('O', "\uE4FC"),
                Map.entry('P', "\uE4FD"), Map.entry('Q', "\uE4FE"), Map.entry('R', "\uE4FF"), Map.entry('S', "\uE500"),
                Map.entry('T', "\uE501"), Map.entry('U', "\uE502"), Map.entry('V', "\uE503"), Map.entry('W', "\uE504"),
                Map.entry('X', "\uE505"), Map.entry('Y', "\uE506"), Map.entry('Z', "\uE507"), Map.entry('[', "\uE508"),
                Map.entry('\\', "\uE509"), Map.entry(']', "\uE50A"), Map.entry('^', "\uE50B"), Map.entry('_', "\uE50C"),
                Map.entry('`', "\uE50D"), Map.entry('a', "\uE50E"), Map.entry('b', "\uE50F"), Map.entry('c', "\uE510"),
                Map.entry('d', "\uE511"), Map.entry('e', "\uE512"), Map.entry('f', "\uE513"), Map.entry('g', "\uE514"),
                Map.entry('h', "\uE515"), Map.entry('i', "\uE516"), Map.entry('j', "\uE517"), Map.entry('k', "\uE518"),
                Map.entry('l', "\uE519"), Map.entry('m', "\uE51A"), Map.entry('n', "\uE51B"), Map.entry('o', "\uE51C"),
                Map.entry('p', "\uE51D"), Map.entry('q', "\uE51E"), Map.entry('r', "\uE51F"), Map.entry('s', "\uE520"),
                Map.entry('t', "\uE521"), Map.entry('u', "\uE522"), Map.entry('v', "\uE523"), Map.entry('w', "\uE524"),
                Map.entry('x', "\uE525"), Map.entry('y', "\uE526"), Map.entry('z', "\uE527"), Map.entry('{', "\uE528"),
                Map.entry('|', "\uE529"), Map.entry('}', "\uE52A"), Map.entry('~', "\uE52B")
        ));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE52C"), Map.entry('"', "\uE52D"), Map.entry('#', "\uE52E"),
                Map.entry('$', "\uE52F"), Map.entry('%', "\uE530"), Map.entry('&', "\uE531"), Map.entry('\'', "\uE532"),
                Map.entry('(', "\uE533"), Map.entry(')', "\uE534"), Map.entry('*', "\uE535"), Map.entry('+', "\uE536"),
                Map.entry(',', "\uE537"), Map.entry('-', "\uE538"), Map.entry('.', "\uE539"), Map.entry('/', "\uE53A"),
                Map.entry('0', "\uE53B"), Map.entry('1', "\uE53C"), Map.entry('2', "\uE53D"), Map.entry('3', "\uE53E"),
                Map.entry('4', "\uE53F"), Map.entry('5', "\uE540"), Map.entry('6', "\uE541"), Map.entry('7', "\uE542"),
                Map.entry('8', "\uE543"), Map.entry('9', "\uE544"), Map.entry(':', "\uE545"), Map.entry(';', "\uE546"),
                Map.entry('<', "\uE547"), Map.entry('=', "\uE548"), Map.entry('>', "\uE549"), Map.entry('?', "\uE54A"),
                Map.entry('@', "\uE54B"), Map.entry('A', "\uE54C"), Map.entry('B', "\uE54D"), Map.entry('C', "\uE54E"),
                Map.entry('D', "\uE54F"), Map.entry('E', "\uE550"), Map.entry('F', "\uE551"), Map.entry('G', "\uE552"),
                Map.entry('H', "\uE553"), Map.entry('I', "\uE554"), Map.entry('J', "\uE555"), Map.entry('K', "\uE556"),
                Map.entry('L', "\uE557"), Map.entry('M', "\uE558"), Map.entry('N', "\uE559"), Map.entry('O', "\uE55A"),
                Map.entry('P', "\uE55B"), Map.entry('Q', "\uE55C"), Map.entry('R', "\uE55D"), Map.entry('S', "\uE55E"),
                Map.entry('T', "\uE55F"), Map.entry('U', "\uE560"), Map.entry('V', "\uE561"), Map.entry('W', "\uE562"),
                Map.entry('X', "\uE563"), Map.entry('Y', "\uE564"), Map.entry('Z', "\uE565"), Map.entry('[', "\uE566"),
                Map.entry('\\', "\uE567"), Map.entry(']', "\uE568"), Map.entry('^', "\uE569"), Map.entry('_', "\uE56A"),
                Map.entry('`', "\uE56B"), Map.entry('a', "\uE56C"), Map.entry('b', "\uE56D"), Map.entry('c', "\uE56E"),
                Map.entry('d', "\uE56F"), Map.entry('e', "\uE570"), Map.entry('f', "\uE571"), Map.entry('g', "\uE572"),
                Map.entry('h', "\uE573"), Map.entry('i', "\uE574"), Map.entry('j', "\uE575"), Map.entry('k', "\uE576"),
                Map.entry('l', "\uE577"), Map.entry('m', "\uE578"), Map.entry('n', "\uE579"), Map.entry('o', "\uE57A"),
                Map.entry('p', "\uE57B"), Map.entry('q', "\uE57C"), Map.entry('r', "\uE57D"), Map.entry('s', "\uE57E"),
                Map.entry('t', "\uE57F"), Map.entry('u', "\uE580"), Map.entry('v', "\uE581"), Map.entry('w', "\uE582"),
                Map.entry('x', "\uE583"), Map.entry('y', "\uE584"), Map.entry('z', "\uE585"), Map.entry('{', "\uE586"),
                Map.entry('|', "\uE587"), Map.entry('}', "\uE588"), Map.entry('~', "\uE589")
        ));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE58A"), Map.entry('"', "\uE58B"), Map.entry('#', "\uE58C"),
                Map.entry('$', "\uE58D"), Map.entry('%', "\uE58E"), Map.entry('&', "\uE58F"), Map.entry('\'', "\uE590"),
                Map.entry('(', "\uE591"), Map.entry(')', "\uE592"), Map.entry('*', "\uE593"), Map.entry('+', "\uE594"),
                Map.entry(',', "\uE595"), Map.entry('-', "\uE596"), Map.entry('.', "\uE597"), Map.entry('/', "\uE598"),
                Map.entry('0', "\uE599"), Map.entry('1', "\uE59A"), Map.entry('2', "\uE59B"), Map.entry('3', "\uE59C"),
                Map.entry('4', "\uE59D"), Map.entry('5', "\uE59E"), Map.entry('6', "\uE59F"), Map.entry('7', "\uE5A0"),
                Map.entry('8', "\uE5A1"), Map.entry('9', "\uE5A2"), Map.entry(':', "\uE5A3"), Map.entry(';', "\uE5A4"),
                Map.entry('<', "\uE5A5"), Map.entry('=', "\uE5A6"), Map.entry('>', "\uE5A7"), Map.entry('?', "\uE5A8"),
                Map.entry('@', "\uE5A9"), Map.entry('A', "\uE5AA"), Map.entry('B', "\uE5AB"), Map.entry('C', "\uE5AC"),
                Map.entry('D', "\uE5AD"), Map.entry('E', "\uE5AE"), Map.entry('F', "\uE5AF"), Map.entry('G', "\uE5B0"),
                Map.entry('H', "\uE5B1"), Map.entry('I', "\uE5B2"), Map.entry('J', "\uE5B3"), Map.entry('K', "\uE5B4"),
                Map.entry('L', "\uE5B5"), Map.entry('M', "\uE5B6"), Map.entry('N', "\uE5B7"), Map.entry('O', "\uE5B8"),
                Map.entry('P', "\uE5B9"), Map.entry('Q', "\uE5BA"), Map.entry('R', "\uE5BB"), Map.entry('S', "\uE5BC"),
                Map.entry('T', "\uE5BD"), Map.entry('U', "\uE5BE"), Map.entry('V', "\uE5BF"), Map.entry('W', "\uE5C0"),
                Map.entry('X', "\uE5C1"), Map.entry('Y', "\uE5C2"), Map.entry('Z', "\uE5C3"), Map.entry('[', "\uE5C4"),
                Map.entry('\\', "\uE5C5"), Map.entry(']', "\uE5C6"), Map.entry('^', "\uE5C7"), Map.entry('_', "\uE5C8"),
                Map.entry('`', "\uE5C9"), Map.entry('a', "\uE5CA"), Map.entry('b', "\uE5CB"), Map.entry('c', "\uE5CC"),
                Map.entry('d', "\uE5CD"), Map.entry('e', "\uE5CE"), Map.entry('f', "\uE5CF"), Map.entry('g', "\uE5D0"),
                Map.entry('h', "\uE5D1"), Map.entry('i', "\uE5D2"), Map.entry('j', "\uE5D3"), Map.entry('k', "\uE5D4"),
                Map.entry('l', "\uE5D5"), Map.entry('m', "\uE5D6"), Map.entry('n', "\uE5D7"), Map.entry('o', "\uE5D8"),
                Map.entry('p', "\uE5D9"), Map.entry('q', "\uE5DA"), Map.entry('r', "\uE5DB"), Map.entry('s', "\uE5DC"),
                Map.entry('t', "\uE5DD"), Map.entry('u', "\uE5DE"), Map.entry('v', "\uE5DF"), Map.entry('w', "\uE5E0"),
                Map.entry('x', "\uE5E1"), Map.entry('y', "\uE5E2"), Map.entry('z', "\uE5E3"), Map.entry('{', "\uE5E4"),
                Map.entry('|', "\uE5E5"), Map.entry('}', "\uE5E6"), Map.entry('~', "\uE5E7")
        ));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE5E8"), Map.entry('"', "\uE5E9"), Map.entry('#', "\uE5EA"),
                Map.entry('$', "\uE5EB"), Map.entry('%', "\uE5EC"), Map.entry('&', "\uE5ED"), Map.entry('\'', "\uE5EE"),
                Map.entry('(', "\uE5EF"), Map.entry(')', "\uE5F0"), Map.entry('*', "\uE5F1"), Map.entry('+', "\uE5F2"),
                Map.entry(',', "\uE5F3"), Map.entry('-', "\uE5F4"), Map.entry('.', "\uE5F5"), Map.entry('/', "\uE5F6"),
                Map.entry('0', "\uE5F7"), Map.entry('1', "\uE5F8"), Map.entry('2', "\uE5F9"), Map.entry('3', "\uE5FA"),
                Map.entry('4', "\uE5FB"), Map.entry('5', "\uE5FC"), Map.entry('6', "\uE5FD"), Map.entry('7', "\uE5FE"),
                Map.entry('8', "\uE5FF"), Map.entry('9', "\uE600"), Map.entry(':', "\uE601"), Map.entry(';', "\uE602"),
                Map.entry('<', "\uE603"), Map.entry('=', "\uE604"), Map.entry('>', "\uE605"), Map.entry('?', "\uE606"),
                Map.entry('@', "\uE607"), Map.entry('A', "\uE608"), Map.entry('B', "\uE609"), Map.entry('C', "\uE60A"),
                Map.entry('D', "\uE60B"), Map.entry('E', "\uE60C"), Map.entry('F', "\uE60D"), Map.entry('G', "\uE60E"),
                Map.entry('H', "\uE60F"), Map.entry('I', "\uE610"), Map.entry('J', "\uE611"), Map.entry('K', "\uE612"),
                Map.entry('L', "\uE613"), Map.entry('M', "\uE614"), Map.entry('N', "\uE615"), Map.entry('O', "\uE616"),
                Map.entry('P', "\uE617"), Map.entry('Q', "\uE618"), Map.entry('R', "\uE619"), Map.entry('S', "\uE61A"),
                Map.entry('T', "\uE61B"), Map.entry('U', "\uE61C"), Map.entry('V', "\uE61D"), Map.entry('W', "\uE61E"),
                Map.entry('X', "\uE61F"), Map.entry('Y', "\uE620"), Map.entry('Z', "\uE621"), Map.entry('[', "\uE622"),
                Map.entry('\\', "\uE623"), Map.entry(']', "\uE624"), Map.entry('^', "\uE625"), Map.entry('_', "\uE626"),
                Map.entry('`', "\uE627"), Map.entry('a', "\uE628"), Map.entry('b', "\uE629"), Map.entry('c', "\uE62A"),
                Map.entry('d', "\uE62B"), Map.entry('e', "\uE62C"), Map.entry('f', "\uE62D"), Map.entry('g', "\uE62E"),
                Map.entry('h', "\uE62F"), Map.entry('i', "\uE630"), Map.entry('j', "\uE631"), Map.entry('k', "\uE632"),
                Map.entry('l', "\uE633"), Map.entry('m', "\uE634"), Map.entry('n', "\uE635"), Map.entry('o', "\uE636"),
                Map.entry('p', "\uE637"), Map.entry('q', "\uE638"), Map.entry('r', "\uE639"), Map.entry('s', "\uE63A"),
                Map.entry('t', "\uE63B"), Map.entry('u', "\uE63C"), Map.entry('v', "\uE63D"), Map.entry('w', "\uE63E"),
                Map.entry('x', "\uE63F"), Map.entry('y', "\uE640"), Map.entry('z', "\uE641"), Map.entry('{', "\uE642"),
                Map.entry('|', "\uE643"), Map.entry('}', "\uE644"), Map.entry('~', "\uE645")
        ));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE646"), Map.entry('"', "\uE647"), Map.entry('#', "\uE648"),
                Map.entry('$', "\uE649"), Map.entry('%', "\uE64A"), Map.entry('&', "\uE64B"), Map.entry('\'', "\uE64C"),
                Map.entry('(', "\uE64D"), Map.entry(')', "\uE64E"), Map.entry('*', "\uE64F"), Map.entry('+', "\uE650"),
                Map.entry(',', "\uE651"), Map.entry('-', "\uE652"), Map.entry('.', "\uE653"), Map.entry('/', "\uE654"),
                Map.entry('0', "\uE655"), Map.entry('1', "\uE656"), Map.entry('2', "\uE657"), Map.entry('3', "\uE658"),
                Map.entry('4', "\uE659"), Map.entry('5', "\uE65A"), Map.entry('6', "\uE65B"), Map.entry('7', "\uE65C"),
                Map.entry('8', "\uE65D"), Map.entry('9', "\uE65E"), Map.entry(':', "\uE65F"), Map.entry(';', "\uE660"),
                Map.entry('<', "\uE661"), Map.entry('=', "\uE662"), Map.entry('>', "\uE663"), Map.entry('?', "\uE664"),
                Map.entry('@', "\uE665"), Map.entry('A', "\uE666"), Map.entry('B', "\uE667"), Map.entry('C', "\uE668"),
                Map.entry('D', "\uE669"), Map.entry('E', "\uE66A"), Map.entry('F', "\uE66B"), Map.entry('G', "\uE66C"),
                Map.entry('H', "\uE66D"), Map.entry('I', "\uE66E"), Map.entry('J', "\uE66F"), Map.entry('K', "\uE670"),
                Map.entry('L', "\uE671"), Map.entry('M', "\uE672"), Map.entry('N', "\uE673"), Map.entry('O', "\uE674"),
                Map.entry('P', "\uE675"), Map.entry('Q', "\uE676"), Map.entry('R', "\uE677"), Map.entry('S', "\uE678"),
                Map.entry('T', "\uE679"), Map.entry('U', "\uE67A"), Map.entry('V', "\uE67B"), Map.entry('W', "\uE67C"),
                Map.entry('X', "\uE67D"), Map.entry('Y', "\uE67E"), Map.entry('Z', "\uE67F"), Map.entry('[', "\uE680"),
                Map.entry('\\', "\uE681"), Map.entry(']', "\uE682"), Map.entry('^', "\uE683"), Map.entry('_', "\uE684"),
                Map.entry('`', "\uE685"), Map.entry('a', "\uE686"), Map.entry('b', "\uE687"), Map.entry('c', "\uE688"),
                Map.entry('d', "\uE689"), Map.entry('e', "\uE68A"), Map.entry('f', "\uE68B"), Map.entry('g', "\uE68C"),
                Map.entry('h', "\uE68D"), Map.entry('i', "\uE68E"), Map.entry('j', "\uE68F"), Map.entry('k', "\uE690"),
                Map.entry('l', "\uE691"), Map.entry('m', "\uE692"), Map.entry('n', "\uE693"), Map.entry('o', "\uE694"),
                Map.entry('p', "\uE695"), Map.entry('q', "\uE696"), Map.entry('r', "\uE697"), Map.entry('s', "\uE698"),
                Map.entry('t', "\uE699"), Map.entry('u', "\uE69A"), Map.entry('v', "\uE69B"), Map.entry('w', "\uE69C"),
                Map.entry('x', "\uE69D"), Map.entry('y', "\uE69E"), Map.entry('z', "\uE69F"), Map.entry('{', "\uE6A0"),
                Map.entry('|', "\uE6A1"), Map.entry('}', "\uE6A2"), Map.entry('~', "\uE6A3")
        ));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE6A4"), Map.entry('"', "\uE6A5"), Map.entry('#', "\uE6A6"),
                Map.entry('$', "\uE6A7"), Map.entry('%', "\uE6A8"), Map.entry('&', "\uE6A9"), Map.entry('\'', "\uE6AA"),
                Map.entry('(', "\uE6AB"), Map.entry(')', "\uE6AC"), Map.entry('*', "\uE6AD"), Map.entry('+', "\uE6AE"),
                Map.entry(',', "\uE6AF"), Map.entry('-', "\uE6B0"), Map.entry('.', "\uE6B1"), Map.entry('/', "\uE6B2"),
                Map.entry('0', "\uE6B3"), Map.entry('1', "\uE6B4"), Map.entry('2', "\uE6B5"), Map.entry('3', "\uE6B6"),
                Map.entry('4', "\uE6B7"), Map.entry('5', "\uE6B8"), Map.entry('6', "\uE6B9"), Map.entry('7', "\uE6BA"),
                Map.entry('8', "\uE6BB"), Map.entry('9', "\uE6BC"), Map.entry(':', "\uE6BD"), Map.entry(';', "\uE6BE"),
                Map.entry('<', "\uE6BF"), Map.entry('=', "\uE6C0"), Map.entry('>', "\uE6C1"), Map.entry('?', "\uE6C2"),
                Map.entry('@', "\uE6C3"), Map.entry('A', "\uE6C4"), Map.entry('B', "\uE6C5"), Map.entry('C', "\uE6C6"),
                Map.entry('D', "\uE6C7"), Map.entry('E', "\uE6C8"), Map.entry('F', "\uE6C9"), Map.entry('G', "\uE6CA"),
                Map.entry('H', "\uE6CB"), Map.entry('I', "\uE6CC"), Map.entry('J', "\uE6CD"), Map.entry('K', "\uE6CE"),
                Map.entry('L', "\uE6CF"), Map.entry('M', "\uE6D0"), Map.entry('N', "\uE6D1"), Map.entry('O', "\uE6D2"),
                Map.entry('P', "\uE6D3"), Map.entry('Q', "\uE6D4"), Map.entry('R', "\uE6D5"), Map.entry('S', "\uE6D6"),
                Map.entry('T', "\uE6D7"), Map.entry('U', "\uE6D8"), Map.entry('V', "\uE6D9"), Map.entry('W', "\uE6DA"),
                Map.entry('X', "\uE6DB"), Map.entry('Y', "\uE6DC"), Map.entry('Z', "\uE6DD"), Map.entry('[', "\uE6DE"),
                Map.entry('\\', "\uE6DF"), Map.entry(']', "\uE6E0"), Map.entry('^', "\uE6E1"), Map.entry('_', "\uE6E2"),
                Map.entry('`', "\uE6E3"), Map.entry('a', "\uE6E4"), Map.entry('b', "\uE6E5"), Map.entry('c', "\uE6E6"),
                Map.entry('d', "\uE6E7"), Map.entry('e', "\uE6E8"), Map.entry('f', "\uE6E9"), Map.entry('g', "\uE6EA"),
                Map.entry('h', "\uE6EB"), Map.entry('i', "\uE6EC"), Map.entry('j', "\uE6ED"), Map.entry('k', "\uE6EE"),
                Map.entry('l', "\uE6EF"), Map.entry('m', "\uE6F0"), Map.entry('n', "\uE6F1"), Map.entry('o', "\uE6F2"),
                Map.entry('p', "\uE6F3"), Map.entry('q', "\uE6F4"), Map.entry('r', "\uE6F5"), Map.entry('s', "\uE6F6"),
                Map.entry('t', "\uE6F7"), Map.entry('u', "\uE6F8"), Map.entry('v', "\uE6F9"), Map.entry('w', "\uE6FA"),
                Map.entry('x', "\uE6FB"), Map.entry('y', "\uE6FC"), Map.entry('z', "\uE6FD"), Map.entry('{', "\uE6FE"),
                Map.entry('|', "\uE6FF"), Map.entry('}', "\uE700"), Map.entry('~', "\uE701")
        ));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE702"), Map.entry('"', "\uE703"), Map.entry('#', "\uE704"),
                Map.entry('$', "\uE705"), Map.entry('%', "\uE706"), Map.entry('&', "\uE707"), Map.entry('\'', "\uE708"),
                Map.entry('(', "\uE709"), Map.entry(')', "\uE70A"), Map.entry('*', "\uE70B"), Map.entry('+', "\uE70C"),
                Map.entry(',', "\uE70D"), Map.entry('-', "\uE70E"), Map.entry('.', "\uE70F"), Map.entry('/', "\uE710"),
                Map.entry('0', "\uE711"), Map.entry('1', "\uE712"), Map.entry('2', "\uE713"), Map.entry('3', "\uE714"),
                Map.entry('4', "\uE715"), Map.entry('5', "\uE716"), Map.entry('6', "\uE717"), Map.entry('7', "\uE718"),
                Map.entry('8', "\uE719"), Map.entry('9', "\uE71A"), Map.entry(':', "\uE71B"), Map.entry(';', "\uE71C"),
                Map.entry('<', "\uE71D"), Map.entry('=', "\uE71E"), Map.entry('>', "\uE71F"), Map.entry('?', "\uE720"),
                Map.entry('@', "\uE721"), Map.entry('A', "\uE722"), Map.entry('B', "\uE723"), Map.entry('C', "\uE724"),
                Map.entry('D', "\uE725"), Map.entry('E', "\uE726"), Map.entry('F', "\uE727"), Map.entry('G', "\uE728"),
                Map.entry('H', "\uE729"), Map.entry('I', "\uE72A"), Map.entry('J', "\uE72B"), Map.entry('K', "\uE72C"),
                Map.entry('L', "\uE72D"), Map.entry('M', "\uE72E"), Map.entry('N', "\uE72F"), Map.entry('O', "\uE730"),
                Map.entry('P', "\uE731"), Map.entry('Q', "\uE732"), Map.entry('R', "\uE733"), Map.entry('S', "\uE734"),
                Map.entry('T', "\uE735"), Map.entry('U', "\uE736"), Map.entry('V', "\uE737"), Map.entry('W', "\uE738"),
                Map.entry('X', "\uE739"), Map.entry('Y', "\uE73A"), Map.entry('Z', "\uE73B"), Map.entry('[', "\uE73C"),
                Map.entry('\\', "\uE73D"), Map.entry(']', "\uE73E"), Map.entry('^', "\uE73F"), Map.entry('_', "\uE740"),
                Map.entry('`', "\uE741"), Map.entry('a', "\uE742"), Map.entry('b', "\uE743"), Map.entry('c', "\uE744"),
                Map.entry('d', "\uE745"), Map.entry('e', "\uE746"), Map.entry('f', "\uE747"), Map.entry('g', "\uE748"),
                Map.entry('h', "\uE749"), Map.entry('i', "\uE74A"), Map.entry('j', "\uE74B"), Map.entry('k', "\uE74C"),
                Map.entry('l', "\uE74D"), Map.entry('m', "\uE74E"), Map.entry('n', "\uE74F"), Map.entry('o', "\uE750"),
                Map.entry('p', "\uE751"), Map.entry('q', "\uE752"), Map.entry('r', "\uE753"), Map.entry('s', "\uE754"),
                Map.entry('t', "\uE755"), Map.entry('u', "\uE756"), Map.entry('v', "\uE757"), Map.entry('w', "\uE758"),
                Map.entry('x', "\uE759"), Map.entry('y', "\uE75A"), Map.entry('z', "\uE75B"), Map.entry('{', "\uE75C"),
                Map.entry('|', "\uE75D"), Map.entry('}', "\uE75E"), Map.entry('~', "\uE75F")
        ));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE760"), Map.entry('"', "\uE761"), Map.entry('#', "\uE762"),
                Map.entry('$', "\uE763"), Map.entry('%', "\uE764"), Map.entry('&', "\uE765"), Map.entry('\'', "\uE766"),
                Map.entry('(', "\uE767"), Map.entry(')', "\uE768"), Map.entry('*', "\uE769"), Map.entry('+', "\uE76A"),
                Map.entry(',', "\uE76B"), Map.entry('-', "\uE76C"), Map.entry('.', "\uE76D"), Map.entry('/', "\uE76E"),
                Map.entry('0', "\uE76F"), Map.entry('1', "\uE770"), Map.entry('2', "\uE771"), Map.entry('3', "\uE772"),
                Map.entry('4', "\uE773"), Map.entry('5', "\uE774"), Map.entry('6', "\uE775"), Map.entry('7', "\uE776"),
                Map.entry('8', "\uE777"), Map.entry('9', "\uE778"), Map.entry(':', "\uE779"), Map.entry(';', "\uE77A"),
                Map.entry('<', "\uE77B"), Map.entry('=', "\uE77C"), Map.entry('>', "\uE77D"), Map.entry('?', "\uE77E"),
                Map.entry('@', "\uE77F"), Map.entry('A', "\uE780"), Map.entry('B', "\uE781"), Map.entry('C', "\uE782"),
                Map.entry('D', "\uE783"), Map.entry('E', "\uE784"), Map.entry('F', "\uE785"), Map.entry('G', "\uE786"),
                Map.entry('H', "\uE787"), Map.entry('I', "\uE788"), Map.entry('J', "\uE789"), Map.entry('K', "\uE78A"),
                Map.entry('L', "\uE78B"), Map.entry('M', "\uE78C"), Map.entry('N', "\uE78D"), Map.entry('O', "\uE78E"),
                Map.entry('P', "\uE78F"), Map.entry('Q', "\uE790"), Map.entry('R', "\uE791"), Map.entry('S', "\uE792"),
                Map.entry('T', "\uE793"), Map.entry('U', "\uE794"), Map.entry('V', "\uE795"), Map.entry('W', "\uE796"),
                Map.entry('X', "\uE797"), Map.entry('Y', "\uE798"), Map.entry('Z', "\uE799"), Map.entry('[', "\uE79A"),
                Map.entry('\\', "\uE79B"), Map.entry(']', "\uE79C"), Map.entry('^', "\uE79D"), Map.entry('_', "\uE79E"),
                Map.entry('`', "\uE79F"), Map.entry('a', "\uE7A0"), Map.entry('b', "\uE7A1"), Map.entry('c', "\uE7A2"),
                Map.entry('d', "\uE7A3"), Map.entry('e', "\uE7A4"), Map.entry('f', "\uE7A5"), Map.entry('g', "\uE7A6"),
                Map.entry('h', "\uE7A7"), Map.entry('i', "\uE7A8"), Map.entry('j', "\uE7A9"), Map.entry('k', "\uE7AA"),
                Map.entry('l', "\uE7AB"), Map.entry('m', "\uE7AC"), Map.entry('n', "\uE7AD"), Map.entry('o', "\uE7AE"),
                Map.entry('p', "\uE7AF"), Map.entry('q', "\uE7B0"), Map.entry('r', "\uE7B1"), Map.entry('s', "\uE7B2"),
                Map.entry('t', "\uE7B3"), Map.entry('u', "\uE7B4"), Map.entry('v', "\uE7B5"), Map.entry('w', "\uE7B6"),
                Map.entry('x', "\uE7B7"), Map.entry('y', "\uE7B8"), Map.entry('z', "\uE7B9"), Map.entry('{', "\uE7BA"),
                Map.entry('|', "\uE7BB"), Map.entry('}', "\uE7BC"), Map.entry('~', "\uE7BD")
        ));
        
        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE7BE"), Map.entry('"', "\uE7BF"), Map.entry('#', "\uE7C0"),
                Map.entry('$', "\uE7C1"), Map.entry('%', "\uE7C2"), Map.entry('&', "\uE7C3"), Map.entry('\'', "\uE7C4"),
                Map.entry('(', "\uE7C5"), Map.entry(')', "\uE7C6"), Map.entry('*', "\uE7C7"), Map.entry('+', "\uE7C8"),
                Map.entry(',', "\uE7C9"), Map.entry('-', "\uE7CA"), Map.entry('.', "\uE7CB"), Map.entry('/', "\uE7CC"),
                Map.entry('0', "\uE7CD"), Map.entry('1', "\uE7CE"), Map.entry('2', "\uE7CF"), Map.entry('3', "\uE7D0"),
                Map.entry('4', "\uE7D1"), Map.entry('5', "\uE7D2"), Map.entry('6', "\uE7D3"), Map.entry('7', "\uE7D4"),
                Map.entry('8', "\uE7D5"), Map.entry('9', "\uE7D6"), Map.entry(':', "\uE7D7"), Map.entry(';', "\uE7D8"),
                Map.entry('<', "\uE7D9"), Map.entry('=', "\uE7DA"), Map.entry('>', "\uE7DB"), Map.entry('?', "\uE7DC"),
                Map.entry('@', "\uE7DD"), Map.entry('A', "\uE7DE"), Map.entry('B', "\uE7DF"), Map.entry('C', "\uE7E0"),
                Map.entry('D', "\uE7E1"), Map.entry('E', "\uE7E2"), Map.entry('F', "\uE7E3"), Map.entry('G', "\uE7E4"),
                Map.entry('H', "\uE7E5"), Map.entry('I', "\uE7E6"), Map.entry('J', "\uE7E7"), Map.entry('K', "\uE7E8"),
                Map.entry('L', "\uE7E9"), Map.entry('M', "\uE7EA"), Map.entry('N', "\uE7EB"), Map.entry('O', "\uE7EC"),
                Map.entry('P', "\uE7ED"), Map.entry('Q', "\uE7EE"), Map.entry('R', "\uE7EF"), Map.entry('S', "\uE7F0"),
                Map.entry('T', "\uE7F1"), Map.entry('U', "\uE7F2"), Map.entry('V', "\uE7F3"), Map.entry('W', "\uE7F4"),
                Map.entry('X', "\uE7F5"), Map.entry('Y', "\uE7F6"), Map.entry('Z', "\uE7F7"), Map.entry('[', "\uE7F8"),
                Map.entry('\\', "\uE7F9"), Map.entry(']', "\uE7FA"), Map.entry('^', "\uE7FB"), Map.entry('_', "\uE7FC"),
                Map.entry('`', "\uE7FD"), Map.entry('a', "\uE7FE"), Map.entry('b', "\uE7FF"), Map.entry('c', "\uE800"),
                Map.entry('d', "\uE801"), Map.entry('e', "\uE802"), Map.entry('f', "\uE803"), Map.entry('g', "\uE804"),
                Map.entry('h', "\uE805"), Map.entry('i', "\uE806"), Map.entry('j', "\uE807"), Map.entry('k', "\uE808"),
                Map.entry('l', "\uE809"), Map.entry('m', "\uE80A"), Map.entry('n', "\uE80B"), Map.entry('o', "\uE80C"),
                Map.entry('p', "\uE80D"), Map.entry('q', "\uE80E"), Map.entry('r', "\uE80F"), Map.entry('s', "\uE810"),
                Map.entry('t', "\uE811"), Map.entry('u', "\uE812"), Map.entry('v', "\uE813"), Map.entry('w', "\uE814"),
                Map.entry('x', "\uE815"), Map.entry('y', "\uE816"), Map.entry('z', "\uE817"), Map.entry('{', "\uE818"),
                Map.entry('|', "\uE819"), Map.entry('}', "\uE81A"), Map.entry('~', "\uE81B")
        ));

        characterStringMapList.add(Map.<Character, String>ofEntries(
                Map.entry(' ', " "), Map.entry('!', "\uE81C"), Map.entry('"', "\uE81D"), Map.entry('#', "\uE81E"),
                Map.entry('$', "\uE81F"), Map.entry('%', "\uE820"), Map.entry('&', "\uE821"), Map.entry('\'', "\uE822"),
                Map.entry('(', "\uE823"), Map.entry(')', "\uE824"), Map.entry('*', "\uE825"), Map.entry('+', "\uE826"),
                Map.entry(',', "\uE827"), Map.entry('-', "\uE828"), Map.entry('.', "\uE829"), Map.entry('/', "\uE82A"),
                Map.entry('0', "\uE82B"), Map.entry('1', "\uE82C"), Map.entry('2', "\uE82D"), Map.entry('3', "\uE82E"),
                Map.entry('4', "\uE82F"), Map.entry('5', "\uE830"), Map.entry('6', "\uE831"), Map.entry('7', "\uE832"),
                Map.entry('8', "\uE833"), Map.entry('9', "\uE834"), Map.entry(':', "\uE835"), Map.entry(';', "\uE836"),
                Map.entry('<', "\uE837"), Map.entry('=', "\uE838"), Map.entry('>', "\uE839"), Map.entry('?', "\uE83A"),
                Map.entry('@', "\uE83B"), Map.entry('A', "\uE83C"), Map.entry('B', "\uE83D"), Map.entry('C', "\uE83E"),
                Map.entry('D', "\uE83F"), Map.entry('E', "\uE840"), Map.entry('F', "\uE841"), Map.entry('G', "\uE842"),
                Map.entry('H', "\uE843"), Map.entry('I', "\uE844"), Map.entry('J', "\uE845"), Map.entry('K', "\uE846"),
                Map.entry('L', "\uE847"), Map.entry('M', "\uE848"), Map.entry('N', "\uE849"), Map.entry('O', "\uE84A"),
                Map.entry('P', "\uE84B"), Map.entry('Q', "\uE84C"), Map.entry('R', "\uE84D"), Map.entry('S', "\uE84E"),
                Map.entry('T', "\uE84F"), Map.entry('U', "\uE850"), Map.entry('V', "\uE851"), Map.entry('W', "\uE852"),
                Map.entry('X', "\uE853"), Map.entry('Y', "\uE854"), Map.entry('Z', "\uE855"), Map.entry('[', "\uE856"),
                Map.entry('\\', "\uE857"), Map.entry(']', "\uE858"), Map.entry('^', "\uE859"), Map.entry('_', "\uE85A"),
                Map.entry('`', "\uE85B"), Map.entry('a', "\uE85C"), Map.entry('b', "\uE85D"), Map.entry('c', "\uE85E"),
                Map.entry('d', "\uE85F"), Map.entry('e', "\uE860"), Map.entry('f', "\uE861"), Map.entry('g', "\uE862"),
                Map.entry('h', "\uE863"), Map.entry('i', "\uE864"), Map.entry('j', "\uE865"), Map.entry('k', "\uE866"),
                Map.entry('l', "\uE867"), Map.entry('m', "\uE868"), Map.entry('n', "\uE869"), Map.entry('o', "\uE86A"),
                Map.entry('p', "\uE86B"), Map.entry('q', "\uE86C"), Map.entry('r', "\uE86D"), Map.entry('s', "\uE86E"),
                Map.entry('t', "\uE86F"), Map.entry('u', "\uE870"), Map.entry('v', "\uE871"), Map.entry('w', "\uE872"),
                Map.entry('x', "\uE873"), Map.entry('y', "\uE874"), Map.entry('z', "\uE875"), Map.entry('{', "\uE876"),
                Map.entry('|', "\uE877"), Map.entry('}', "\uE878"), Map.entry('~', "\uE879")
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
