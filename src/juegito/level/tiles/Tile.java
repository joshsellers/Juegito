package juegito.level.tiles;

import juegito.gfx.Screen;
import juegito.level.Level;

/**
 *
 * @author joshsellers
 */
public abstract class Tile {
    
    public static Tile[] tiles = new Tile[256];
    
    public static final BasicTile VOID = new BasicTile(0, 0, 0, 0xFF000000, true, 0x00, false);
    public static final BasicTile GRASS_0 = new BasicTile(1, 1, 0, 0xFF00FF00, false, 0x00, false);
    public static final BasicTile PATH_0_0 = new BasicTile(2, 2, 0, 0xFFD8C880, false, 0x00, false);
    public static final BasicTile PATH_0_1 = new BasicTile(3, 3, 0, 0xFFD8C881, false, 0x00, false);
    public static final BasicTile PATH_0_2 = new BasicTile(4, 4, 0, 0xFFD8C882, false, 0x00, false);
    public static final BasicTile PATH_0_3 = new BasicTile(5, 2, 1, 0xFFD8C883, false, 0x00, false);
    public static final BasicTile PATH_0_4 = new BasicTile(6, 3, 1, 0xFFD8C884, false, 0x00, false);
    public static final BasicTile PATH_0_5 = new BasicTile(7, 4, 1, 0xFFD8C885, false, 0x00, false);
    public static final BasicTile PATH_0_6 = new BasicTile(8, 2, 2, 0xFFD8C886, false, 0x00, false);
    public static final BasicTile PATH_0_7 = new BasicTile(9, 3, 2, 0xFFD8C887, false, 0x00, false);
    public static final BasicTile PATH_0_8 = new BasicTile(10, 4, 2, 0xFFD8C888, false, 0x00, false);
    public static final BasicTile PATH_1_0 = new BasicTile(11, 5, 0, 0xFFA0D0C0, false, 0x00, false);
    public static final BasicTile PATH_1_1 = new BasicTile(12, 6, 0, 0xFFA0D0C1, false, 0x00, false);
    public static final BasicTile PATH_1_2 = new BasicTile(13, 7, 0, 0xFFA0D0C2, false, 0x00, false);
    public static final BasicTile PATH_1_3 = new BasicTile(14, 5, 1, 0xFFA0D0C3, false, 0x00, false);
    public static final BasicTile PATH_1_4 = new BasicTile(15, 6, 1, 0xFFA0D0C4, false, 0x00, false);
    public static final BasicTile PATH_1_5 = new BasicTile(16, 7, 1, 0xFFA0D0C5, false, 0x00, false);
    public static final BasicTile PATH_1_6 = new BasicTile(17, 5, 2, 0xFFA0D0C6, false, 0x00, false);
    public static final BasicTile PATH_1_7 = new BasicTile(18, 6, 2, 0xFFA0D0C7, false, 0x00, false);
    public static final BasicTile PATH_1_8 = new BasicTile(19, 7, 2, 0xFFA0D0C8, false, 0x00, false);
    public static final BasicTile ROCK_0 = new BasicTile(20, 8, 0, 0xFF805858, true, 0x00, false);
    public static final BasicTile UNUSED  = new BasicTile(21, 9, 0, 0xFF70FF00, true, 0x00, false);
    public static final BasicTile FLOOR_0_0 = new BasicTile(22, 8, 1, 0xFFDBBB60, false, 0x00, false);
    public static final BasicTile FLOOR_0_1 = new BasicTile(23, 9, 1, 0xFFDBBB61, false, 0x00, false);
    public static final BasicTile TABLE_0_0 = new BasicTile(24, 8, 2, 0xFFC0A060, true, 0x00, false);
    public static final BasicTile TABLE_0_1 = new BasicTile(25, 9, 2, 0xFFC0A061, true, 0x00, false);
    public static final BasicTile TABLE_0_2 = new BasicTile(26, 8, 3, 0xFFC0A062, true, 0x00, false);
    public static final BasicTile TABLE_0_3 = new BasicTile(27, 9, 3, 0xFFC0A063, true, 0x00, false);
    public static final BasicTile TABLE_0_4 = new BasicTile(28, 6, 3, 0xFFC0A064, true, 0x00, false);
    public static final BasicTile TABLE_0_5 = new BasicTile(29, 7, 3, 0xFFC0A065, true, 0x00, false);
    public static final BasicTile WALL_0_0 = new BasicTile(30, 15, 2, 0xFF909090, true, 0x00, false);
    public static final BasicTile WALL_0_1 = new BasicTile(31, 14, 3, 0xFF909091, true, 0x00, false);
    public static final BasicTile FURNACE_0_0 = new AnimatedTile(32, new int[][] {{13, 2}, {13, 5}, {13, 6}, {13, 7}}, 0xFFE0E0B0, 50, true, 0x00, false);
    public static final BasicTile FURNACE_0_1 = new AnimatedTile(33, new int[][] {{14, 2}, {14, 5}, {14, 6}, {14, 7}}, 0xFFE0E0B1, 50, true, 0x00, false);
    public static final BasicTile FURNACE_0_2 = new BasicTile(34, 15, 3, 0xFFE0E0B2, true, 0x00, false);
    public static final BasicTile FURNACE_0_3 = new BasicTile(35, 16, 3, 0xFFE0E0B3, true, 0x00, false);
    public static final BasicTile BOOKSHELF_0_0 = new BasicTile(36, 16, 2, 0xFFF8F8F0, true, 0x00, false);
    public static final BasicTile BOOKSHELF_0_1 = new BasicTile(37, 13, 3, 0xFFF8F8F1, true, 0x00, false);
    public static final BasicTile BED_0_0 = new BasicTile(38, 16, 0, 0xFFF00000, true, 0x00, false);
    public static final BasicTile BED_0_1 = new BasicTile(39, 16, 1, 0xFFF00001, false, 0x00, false);
    public static final BasicTile TABLE_0_WITH_NOTE = new BasicTile(40, 5, 3, 0xFFC0A066, true, 0x00, false);
    public static final BasicTile PORTAL_0 = new BasicTile(41, 4, 3, 0xFF5DFFCC, false, 0x00, false);
    public static final BasicTile CABINET_0_0 = new BasicTile(42, 0, 1, 0xFF9090F0, true, 0x00, false);
    public static final BasicTile CABINET_0_1 = new BasicTile(43, 1, 1, 0xFF9090F1, true, 0x00, false);
    public static final BasicTile CABINET_0_2 = new BasicTile(44, 0, 2, 0xFF9090F2, true, 0x00, false);
    public static final BasicTile CABINET_0_3 = new BasicTile(45, 1, 2, 0xFF9090F3, true, 0x00, false);
    public static final AnimatedTile WATER = new AnimatedTile(46, new int[][] {{3, 3}, {3, 4}, {3, 5}, {3, 6}, {3, 7}, {3, 8}, {3, 9}, {3, 10}}, 0xFF0000FF, 500, false, 0x00, false);
    public static final BasicTile GRASS_1 = new BasicTile(47, 1, 3, 0xFF00FF01, false, 0x00, false);
    public static final BasicTile GRASS_2 = new BasicTile(48, 1, 4, 0xFF00FF02, false, 0x00, false);
    public static final BasicTile GRASS_3 = new BasicTile(49, 2, 3, 0xFF00FF03, false, 0x00, false);
    public static final BasicTile GRASS_4 = new BasicTile(50, 2, 4, 0xFF00FF04, false, 0x00, false);
    public static final BasicTile TREE_0_0 = new BasicTile(51, 0, 5, 0xFFFFFF00, false, 0x00, false);
    public static final BasicTile TREE_0_1 = new BasicTile(52, 1, 5, 0xFFFFFF01, false, 0x00, false);
    public static final BasicTile TREE_0_2 = new BasicTile(53, 0, 6, 0xFFFFFF02, false, 0x00, false);
    public static final BasicTile TREE_0_3 = new BasicTile(54, 1, 6, 0xFFFFFF03, false, 0x00, false);
    public static final BasicTile TREE_0_4 = new BasicTile(55, 0, 7, 0xFFFFFF04, true, 0x00, false);
    public static final BasicTile TREE_0_5 = new BasicTile(56, 1, 7, 0xFFFFFF05, true, 0x00, false);
    public static final BasicTile DOOR_0_0 = new BasicTile(57, 0, 3, 0xFFA0A0A0, true, 0x00, false);
    public static final BasicTile DOOR_0_1 = new BasicTile(58, 0, 4, 0xFFA0A0A1, false, 0x00, false);
    public static final BasicTile EXIT = new BasicTile(59, 4, 4, 0xFFEE5020, false, 0x00, false); 
    public static final BasicTile AIR = new BasicTile(60, 0, 0, 0xFF000001, false, 0x00, false);
    public static final BasicTile SIGNPOST_0  = new BasicTile(61, 10, 0, 0xFF70FF01, false, 0x00, false);
    public static final BasicTile SIGNPOST_1  = new BasicTile(62, 10, 1, 0xFF70FF02, true, 0x00, false);
    public static final BasicTile G_WATER_BORDER_0 = new BasicTile(63, 5, 4, 0xFF503c20, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_1 = new BasicTile(64, 6, 4, 0xFF503c21, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_2 = new BasicTile(65, 7, 4, 0xFF503c22, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_3 = new BasicTile(66, 5, 5, 0xFF503c23, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_4 = new BasicTile(67, 7, 5, 0xFF503c24, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_5 = new BasicTile(68, 5, 6, 0xFF503c25, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_6 = new BasicTile(69, 6, 6, 0xFF503c26, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_7 = new BasicTile(70, 7, 6, 0xFF503c27, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_8 = new BasicTile(71, 6, 5, 0xFF503c28, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_9 = new BasicTile(72, 5, 7, 0xFF503c29, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_10 = new BasicTile(73, 6, 7, 0xFF503c2A, false, 0x00, false);
    public static final BasicTile G_WATER_BORDER_11 = new BasicTile(74, 7, 7, 0xFF503c2B, false, 0x00, false);
    public static final BasicTile SAND = new BasicTile(75, 8, 4, 0xFFDCD780, false, 0x00, false);
    public static final BasicTile TREE_1_0 = new BasicTile(76, 0, 8, 0xFFFFFF08, false, 0x00, false);
    public static final BasicTile TREE_1_1 = new BasicTile(77, 1, 8, 0xFFFFFF09, false, 0x00, false);
    public static final BasicTile TREE_1_2 = new BasicTile(78, 0, 9, 0xFFFFFF0A, false, 0x00, true);
    public static final BasicTile TREE_1_3 = new BasicTile(79, 1, 9, 0xFFFFFF0B, false, 0x00, true);
    public static final BasicTile TREE_1_4 = new BasicTile(80, 0, 10, 0xFFFFFF0C, true, 0x00, false);
    public static final BasicTile TREE_1_5 = new BasicTile(81, 1, 10, 0xFFFFFF0D, true, 0x00, false);
    public static final BasicTile BRIDGE_0_0 = new BasicTile(82, 11, 0, 0xFF835E00, false, 0x00, false);
    public static final BasicTile BRIDGE_0_1 = new BasicTile(83, 12, 0, 0xFF835E01, false, 0x00, false);
    public static final BasicTile BRIDGE_0_2 = new BasicTile(84, 13, 0, 0xFF835E02, false, 0x00, false);
    public static final BasicTile BRIDGE_0_3 = new BasicTile(85, 14, 0, 0xFF835E03, false, 0x00, false);
    public static final BasicTile BRIDGE_0_4 = new BasicTile(86, 15, 0, 0xFF835E04, false, 0x00, false);
    public static final BasicTile BRIDGE_0_5 = new BasicTile(87, 11, 1, 0xFF835E05, false, 0x00, false);
    public static final BasicTile BRIDGE_0_6 = new BasicTile(88, 12, 1, 0xFF835E06, false, 0x00, false);
    public static final BasicTile BRIDGE_0_7 = new BasicTile(89, 13, 1, 0xFF835E07, false, 0x00, false);
    public static final BasicTile BRIDGE_0_8 = new BasicTile(90, 14, 1, 0xFF835E08, false, 0x00, false);
    public static final BasicTile BRIDGE_0_9 = new BasicTile(91, 15, 1, 0xFF835E09, false, 0x00, false);
    public static final BasicTile CHEST_0_0 = new BasicTile(92, 2, 7, 0xFF7B6D60, true, 0x00, false);
    public static final AnimatedTile WATER_SOLID = new AnimatedTile(93, new int[][] {{3, 3}, {3, 4}, {3, 5}, {3, 6}, {3, 7}}, 0xFF7B6D61, 500, true, 0x00, false);
    public static final Tile LOG_0 = new BasicTile(94, 7, 8, 0xFF483035, true, 0x00, false);
    public static final Tile LOG_1 = new BasicTile(95, 8, 8, 0xFF483036, true, 0x00, false);
    public static final Tile LOG_2 = new BasicTile(96, 9, 8, 0xFF483037, true, 0x00, false);
    public static final Tile ROCK_1 = new BasicTile(97, 10, 8, 0xFF483038, true, 0x00, false);
    public static final Tile ROCK_2 = new BasicTile(98, 12, 8, 0xFF483039, true, 0x00, false);
    public static final Tile ROCK_3 = new BasicTile(99, 13, 8, 0xFF48303A, true, 0x00, false);
    public static final Tile ROCK_4 = new BasicTile(100, 14, 8, 0xFF48303B, true, 0x00, false);
    public static final Tile TILLED_GRASS_0 = new BasicTile(101, 15, 8, 0xFF48303C, false, 0x00, false);
    public static final Tile TILLED_GRASS_1 = new BasicTile(102, 16, 8, 0xFF48303D, false, 0x00, false);
    public static final Tile TILLED_GRASS_2 = new BasicTile(103, 15, 9, 0xFF48303E, false, 0x00, false);
    public static final Tile TILLED_GRASS_3 = new BasicTile(104, 16, 9, 0xFF48303F, false, 0x00, false);
    public static final Tile COOP_0 = new BasicTile(105, 17, 8, 0xFF483040, false, 0x00, true);
    public static final Tile COOP_1 = new BasicTile(106, 18, 8, 0xFF483041, false, 0x00, true);
    public static final Tile COOP_2 = new BasicTile(107, 17, 9, 0xFF483042, false, 0x00, true);
    public static final Tile COOP_3 = new BasicTile(108, 18, 9, 0xFF483043, false, 0x00, true);
    public static final Tile COOP_4 = new BasicTile(109, 17, 10, 0xFF483044, true, 0x00, false);
    public static final Tile COOP_5 = new BasicTile(110, 18, 10, 0xFF483045, true, 0x00, false);
    public static final Tile BRIDGE_1_0 = new BasicTile(111, 9, 9, 0xFF483046, false, 0x00, false);
    public static final Tile BRIDGE_1_1 = new BasicTile(112, 10, 9, 0xFF483047, false, 0x00, false);
    public static final Tile BRIDGE_1_2 = new BasicTile(113, 11, 9, 0xFF483048, false, 0x00, false);
    public static final Tile BRIDGE_1_3 = new BasicTile(114, 9, 10, 0xFF483049, true, 0x00, true);
    public static final Tile BRIDGE_1_4 = new BasicTile(115, 10, 10, 0xFF48304A, false, 0x00, false);
    public static final Tile BRIDGE_1_5 = new BasicTile(116, 11, 10, 0xFF48304B, true, 0x00, true);
    public static final Tile BRIDGE_1_6 = new BasicTile(117, 9, 11, 0xFF48304C, false, 0x00, false);
    public static final Tile BRIDGE_1_7 = new BasicTile(118, 10, 11, 0xFF48304D, false, 0x00, false);
    public static final Tile BRIDGE_1_8 = new BasicTile(119, 11, 11, 0xFF48304E, false, 0x00, false);
    public static final Tile BRIDGE_2_0 = new BasicTile(120, 12, 9, 0xFF48304F, false, 0x00, false);
    public static final Tile BRIDGE_2_1 = new BasicTile(121, 13, 9, 0xFF483050, true, 0x00, false);
    public static final Tile BRIDGE_2_2 = new BasicTile(122, 14, 9, 0xFF483051, false, 0x00, false);
    public static final Tile BRIDGE_2_3 = new BasicTile(123, 12, 10, 0xFF483052, false, 0x00, false);
    public static final Tile BRIDGE_2_4 = new BasicTile(124, 13, 10, 0xFF483053, false, 0x00, false);
    public static final Tile BRIDGE_2_5 = new BasicTile(125, 14, 10, 0xFF483054, false, 0x00, false);
    public static final Tile BRIDGE_2_6 = new BasicTile(126, 12, 11, 0xFF483055, false, 0x00, false);
    public static final Tile BRIDGE_2_7 = new BasicTile(127, 13, 11, 0xFF483056, true, 0x00, true);
    public static final Tile BRIDGE_2_8 = new BasicTile(128, 14, 11, 0xFF483057, false, 0x00, false);
    public static final Tile ROCK_WATER_0_0 = new BasicTile(129, 15, 10, 0xFF483058, false, 0x00, true);
    public static final Tile ROCK_WATER_0_1 = new BasicTile(130, 16, 10, 0xFF483059, false, 0x00, true);
    public static final Tile ROCK_WATER_0_2 = new BasicTile(131, 15, 11, 0xFF48305A, false, 0x00, false);
    public static final Tile ROCK_WATER_0_3 = new BasicTile(132, 16, 11, 0xFF48305B, false, 0x00, false);
    public static final Tile ROCK_WATER_1_0 = new BasicTile(133, 17, 11, 0xFF48305C, false, 0x00, true);
    public static final Tile ROCK_WATER_1_1 = new BasicTile(134, 18, 11, 0xFF48305D, false, 0x00, true);
    public static final Tile ROCK_WATER_1_2 = new BasicTile(135, 17, 12, 0xFF48305E, false, 0x00, false);
    public static final Tile ROCK_WATER_1_3 = new BasicTile(136, 18, 12, 0xFF48305F, false, 0x00, false);
    public static final Tile ROCK_WATER_2_0 = new BasicTile(137, 15, 12, 0xFF483060, false, 0x00, false);
    public static final Tile ROCK_WATER_2_1 = new BasicTile(138, 16, 12, 0xFF483061, false, 0x00, false);
    public static final Tile ROCK_WATER_3_0 = new BasicTile(139, 15, 13, 0xFF483062, false, 0x00, false);
    public static final Tile ROCK_WATER_3_1 = new BasicTile(140, 16, 13, 0xFF483063, false, 0x00, false);
    public static final Tile ROCK_WATER_3_2 = new BasicTile(141, 17, 13, 0xFF483064, false, 0x00, false);
    public static final Tile ROCK_WATER_3_3 = new BasicTile(142, 18, 13, 0xFF483065, false, 0x00, false);
    public static final Tile FLOWERS_0 = new BasicTile(143, 4, 11, 0xFF483066, false, 0x00, false);
    public static final Tile FLOWERS_1 = new BasicTile(144, 5, 11, 0xFF483067, false, 0x00, false);
    public static final Tile IDK_0 = new BasicTile(145, 4, 12, 0xFF483068, false, 0x00, false);
    public static final Tile IDK_1 = new BasicTile(146, 5, 12, 0xFF483069, false, 0x00, false);
    public static final Tile IDK_2 = new BasicTile(147, 4, 13, 0xFF48306A, false, 0x00, false);
    public static final Tile IDK_3 = new BasicTile(148, 5, 14, 0xFF48306B, false, 0x00, false);
    public static final Tile CLIFF_0 = new BasicTile(149, 4, 14, 0xFF48306C, false, 0x00, true);
    public static final Tile CLIFF_1 = new BasicTile(150, 5, 14, 0xFF48306D, false, 0x00, true);
    public static final Tile CLIFF_2 = new BasicTile(151, 6, 14, 0xFF48306E, false, 0x00, true);
    public static final Tile FLOWERS_2 = new BasicTile(152, 7, 14, 0xFF48306F, false, 0x00, false);
    public static final Tile FLOWERS_3 = new BasicTile(153, 6, 15, 0xFF483070, false, 0x00, false);
    public static final Tile FLOWERS_4 = new BasicTile(154, 7, 15, 0xFF483071, false, 0x00, false);
    public static final Tile CLIFF_3 = new BasicTile(155, 4, 15, 0xFF483072, false, 0x00, true);
    public static final Tile CLIFF_4 = new BasicTile(156, 5, 15, 0xFF483073, false, 0x00, true);
    public static final Tile CLIFF_5 = new BasicTile(157, 4, 16, 0xFF483074, false, 0x00, true);
    public static final Tile BUSH_0 = new BasicTile(158, 5, 16, 0xFF483075, true, 0x00, false);
    public static final Tile BUSH_1 = new BasicTile(159, 6, 16, 0xFF483076, true, 0x00, false);  
    public static final Tile BUSH_2 = new BasicTile(160, 7, 16, 0xFF483077, true, 0x00, false);
    public static final Tile BUSH_3 = new BasicTile(161, 4, 17, 0xFF483078, true, 0x00, false);
    public static final Tile BUSH_4 = new BasicTile(162, 5, 17, 0xFF483079, true, 0x00, false);
    public static final Tile BUSH_5 = new BasicTile(163, 6, 17, 0xFF48307A, true, 0x00, false);
    public static final Tile BUSH_6 = new BasicTile(164, 7, 17, 0xFF48307B, true, 0x00, false);
    public static final Tile BUSH_7 = new BasicTile(165, 4, 18, 0xFF48307C, true, 0x00, false);
    public static final Tile BUSH_8 = new BasicTile(166, 5, 18, 0xFF48307D, true, 0x00, false);
    public static final Tile BUSH_9 = new BasicTile(167, 6, 18, 0xFF48307E, true, 0x00, false);
    public static final Tile BUSH_10 = new BasicTile(168, 7, 18, 0xFF48307F, true, 0x00, false);
    public static final Tile CLIFF_6 = new BasicTile(169, 8, 12, 0xFF483080, true, 0x00, false);
    public static final Tile CLIFF_7 = new BasicTile(170, 9, 12, 0xFF483081, true, 0x00, false);
    public static final Tile CLIFF_8 = new BasicTile(171, 10, 12, 0xFF483082, true, 0x00, false);
    public static final Tile CLIFF_9 = new BasicTile(172, 8, 13, 0xFF483083, false, 0x00, false);
    public static final Tile CLIFF_10 = new BasicTile(173, 9, 13, 0xFF483084, true, 0x00, false);
    public static final Tile CLIFF_11 = new BasicTile(174, 10, 13, 0xFF483085, true, 0x00, false);
    public static final Tile CLIFF_12 = new BasicTile(175, 8, 14, 0xFF483086, true, 0x00, false);
    public static final Tile CLIFF_13 = new BasicTile(176, 9, 14, 0xFF483087, true, 0x00, false);
    public static final Tile CLIFF_14 = new BasicTile(177, 10, 14, 0xFF483088, true, 0x00, false);
    public static final Tile CLIFF_15 = new BasicTile(178, 8, 15, 0xFF483089, true, 0x00, false);
    public static final Tile CLIFF_16 = new BasicTile(179, 9, 15, 0xFF48308A, true, 0x00, false);
    public static final Tile CLIFF_17 = new BasicTile(180, 10, 15, 0xFF48308B, true, 0x00, false);
    public static final Tile CLIFF_18 = new BasicTile(181, 8, 16, 0xFF48308C, true, 0x00, false);
    public static final Tile CLIFF_19 = new BasicTile(182, 9, 16, 0xFF48308D, true, 0x00, false);
    public static final Tile CLIFF_20 = new BasicTile(183, 10, 16, 0xFF48308E, true, 0x00, false);
    public static final Tile CLIFF_21 = new BasicTile(184, 8, 17, 0xFF48308F, true, 0x00, false);
    public static final Tile CLIFF_22 = new BasicTile(185, 9, 17, 0xFF483090, true, 0x00, false);
    public static final Tile CLIFF_23 = new BasicTile(186, 10, 17, 0xFF483091, true, 0x00, false);
    public static final Tile CLIFF_24 = new BasicTile(187, 8, 18, 0xFF483092, false, 0x00, false);
    public static final Tile CLIFF_25 = new BasicTile(188, 9, 18, 0xFF483093, false, 0x00, false);
    public static final Tile CLIFF_26 = new BasicTile(189, 10, 18, 0xFF483094, false, 0x00, false);
    public static final Tile CLIFF_27 = new BasicTile(190, 11, 14, 0xFF483095, true, 0x00, false);
    public static final Tile CLIFF_28 = new BasicTile(191, 11, 15, 0xFF483096, true, 0x00, false);
    public static final Tile CLIFF_29 = new BasicTile(192, 11, 16, 0xFF483097, false, 0x00, false);
    public static final Tile CLIFF_30 = new BasicTile(193, 11, 17, 0xFF483098, false, 0x00, false);
    public static final Tile CLIFF_31 = new BasicTile(194, 11, 18, 0xFF483099, false, 0x00, false);
    public static final Tile MANAROCK_0 = new AnimatedTile(195, new int[][] {{12, 4}, {12, 5}, {12, 6}, {12, 7}}, 0xFF48309A, 100, true, 0x00, false);
    public static final Tile TOSTYPLANT_0 = new AnimatedTile(196, new int[][] {{11, 2}}, 0xFF48309B, 1, false, 0x00, true);
    public static final Tile TOSTYPLANT_1 = new AnimatedTile(197, new int[][] {{11, 3}}, 0xFF48309C, 1, false, 0x00, false);
    

    protected int ID;
    protected int color;
    protected boolean solid;
    private boolean animate = false;
    private boolean top;
    
    public Tile(int ID, int color, boolean solid, boolean top) {
        this.ID = ID;
        if (tiles[ID] != null) {
            throw new RuntimeException("Duplicate tile ID: " + ID);
        }
        this.color = color;
        this.solid = solid;
        this.top = top;
        
        tiles[ID] = this;
    }
    
    public abstract void tick();
    
    public abstract void render(Screen screen, Level level, int x, int y, int hue);
    
    public int getID() {
        return ID;
    }
    
    public int getColor() {
        return color;
    }
    
    public boolean getSolid() {
        return solid;
    }
    
    public static Tile getTile(int ID) {
        return tiles[ID];
    }
    
    public static Tile[] getTiles() {
        return tiles;
    }
    
    public void trigger() {
        animate = true;
    }
    
    public void stop() {
        animate = false;
    }
    
    public boolean getAnimating() {
        return animate;
    }
    
    public boolean getTop() {
        return top;
    }
}
