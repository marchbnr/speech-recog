package muster.unittests.preproc;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import muster.util.DoublesParser;

public class ParserTests extends TestCase {
	

	public void testParser() throws NumberFormatException, IOException{
		double[] vals = {0.4451684815556256, 0.4195633492588758, 0.2666921804936762, 0.1833185132688367, 0.043031066368108356, 0.08059035658302821, 0.12303808836255345, 0.11003681783113826, 0.07395059188276008, 0.04513890829042723, -0.007215871973795482, -0.03405423990980423, 0.566033356990767, 0.5545353023582221, 0.3911525166328106, 0.3482143368546459, 0.17948233279218725, 0.12931861501146938, 0.11751596372699835, 0.08107976794741012, -1.6821674379965463E-4, -0.03888800764774009, -0.033850092544112885, -0.0596390948688767, 0.6967090130976855, 0.5781476241517223, 0.321769772709651, 0.1705667475593065, -0.2601093971105961, -0.3382920127678073, -0.22588567221434086, -0.14674524045590265, -0.04451602372391485, 0.1335924142342725, 0.16972198998066115, 0.09323503053716557, 1.4025604002893828, 1.0942058126993601, 0.834165026269751, 0.9467535069788885, 0.21384976596934527, 0.27240749288107785, 0.34116568632670585, 0.0497322836573554, 0.06572679266691601, -0.01078560096691784, -0.14393516854374527, -0.15040176336516514, 2.0893165558518625, 0.5396778223935971, 0.7390085965521257, 1.2999510015903384, -0.44304105479420947, 0.2484200868979765, 0.02529193851349276, -0.2242605978823226, -0.1708623998544751, -0.332830438735072, -0.044809125467567046, -0.198502250992307, 6.876813536991157, 5.3150235794061, 4.565349242434987, 4.273904700461793, 0.7321919333014327, 0.7419819439186591, 0.3955649679663437, -0.2885113342322145, -0.6154480517518046, -0.1619697773318563, -0.08341156103801844, -0.36764406070683453, 10.766230858105454, 5.932047075954217, 5.283085725227693, 5.2342893409984494, -0.08615316051772806, 0.22335913981493907, 0.057320877317810506, -0.1724437168929223, -0.7940555753929627, 0.5888963071876869, 0.4024811486990347, -0.25590875153238235, 11.033102102581987, 6.490731186543, 4.674613655159871, 5.453501198702457, -0.260382767975534, -0.17758915621197868, 0.46613721274671405, -0.4797661804910449, -0.848382151476988, 0.5414447137187524, 0.0859784281304439, -0.29190539319142605, 9.85490958304951, 5.95382803616571, 3.701332985194673, 5.433745004581713, 0.12897229010309055, -0.5383306899476575, 0.5475577866775916, -0.5157910403999889, -1.0057987950390073, -0.19137263810268546, 0.26690980836389516, -0.4858840223177644, 8.049115399113305, 6.7697176815514135, 3.79823720235889, 4.286835711756861, 2.0442676883112614, -0.7711451888956898, 0.4833863029576653, 0.15862532594947595, -0.9528060813647645, -0.8335277965458028, -0.1837081161331259, -0.5520146302709948, 8.781205709212013, 7.152107965683348, 3.507979622366469, 4.619374023790031, 2.3818556203655574, -0.59773141003916, 0.1255572679243795, 0.2624060768970367, -1.159108965708271, -1.5017210323483874, -0.01648410406828997, -0.38799590150400914, 8.388921337599692, 7.338325442794221, 3.4801963965222034, 3.982086022160314, 1.9648008580024012, -0.6534806182017265, -0.19992438576150096, 0.5246076088581532, -0.6487067371318606, -1.2219334157027666, 0.04441708201151054, -0.16774767295594056, 14.026106823644515, 8.598053637551274, 1.8128947029889988, 3.210494916824246, -0.001654155848423003, -1.7458010520224312, -1.6038606483061786, 0.4685363402791956, -1.441638367833244, -1.3059442099841305, 0.7794605008583403, -0.16982538730780736, 15.93554804618551, 8.870180970198906, 0.8606927796815441, 2.1269361655792807, -0.41322650853818826, -1.9974663237243202, -1.8973897455041024, 0.9921479447879774, -1.4321162277598778, -0.9409080893025759, 0.7469332402876219, 0.06995157197050472, 15.999626309228548, 8.791941375794341, 0.3151620298270842, 1.958812118696053, -0.1885090810129747, -2.295874160478053, -1.7501071686477365, 1.3142977845912576, -1.3450493618791697, -0.6762664614007913, 0.8438407331383916, -0.07587897902959698, 15.404278730462545, 8.407447228824104, 0.5833171131232239, 2.0481334522916277, -0.17883701310022865, -2.1919003176828404, -1.794196239837399, 1.1863673723198749, -1.3024738693269544, -0.4656480482086045, 0.9210596518417309, -0.16175714782672612, 15.103382745437028, 7.866811567527067, 0.9851007919166276, 2.7095604938751716, -0.2412911723161748, -2.3762907997429243, -1.302045028180868, 1.0993791770504762, -1.9135125401734647, -0.4559862570987967, 0.9810849340850727, -0.5282887774330429, 14.205175993178162, 7.380212547878266, 2.368534007456996, 3.5399250691148905, -0.20229879460772093, -1.8921701042198777, -1.1418644584829856, 0.3776398746435586, -2.6943768595362756, -0.47776022074514723, 0.5539371936469448, -0.31010038495899833, 11.657049086980447, 6.5050652819116905, 3.7814700256718456, 3.643896202203546, -0.03573057021095151, -0.6982470630816102, -0.5202268412534735, -0.18465290322032868, -2.274562630999161, 0.11377911156751427, -0.1607959104662883, 0.03875215132344333, 10.624739688312411, 6.214226164212323, 4.559533454077848, 4.509287696305924, 0.1989761707421513, -0.5676044664822693, 0.2336142302083549, -0.4243150032406653, -2.0887375192216253, 0.4314658798354394, -0.7760544858016433, -0.08195249265482579, 8.770604365947488, 6.141183890453585, 4.8558796634104295, 4.849492813504765, 0.7775560869461839, -0.3230146857908698, 0.633824904546874, -0.6331973684331377, -1.5085584964672194, 0.37779715977486994, -1.053682716376656, -0.1720000032798791, 6.909996785046092, 6.637470711226804, 4.9889720495611245, 3.839042957994172, 0.9319576287190328, 0.04093152390516027, 0.3463164389537301, -0.7060894511404627, -0.810190806181654, -0.02159677246504728, -0.726769482657313, 0.04454075152549031, 5.264967483533057, 6.253045823817164, 4.847180189731417, 3.3978790864815993, 1.5912027246760778, 0.6680756490074083, 0.2475691595702176, -0.33445463854574586, -0.5024016716694576, -0.45460466879636807, -0.6508620628757973, -0.4582011429594516, 3.909355110673562, 4.699029188975954, 3.9662659506475615, 3.0274522230377277, 1.9047122964022711, 1.103770748080705, 0.4377709963095019, -0.22530180314458517, -0.6515878349320061, -0.7211218397836651, -0.9462051580906218, -0.6753999972032949, 2.8275473512813423, 3.195033720558607, 2.803140696301896, 1.9349828615583793, 1.1960998930477496, 0.48811599261469707, -0.16456667635946812, -0.3048824725427818, -0.5781190809746762, -0.4652088786444869, -0.5992798489267385, -0.3051860677524662, 2.4935523054640933, 2.487096809484912, 2.1699198848909447, 1.106102035991644, 0.4292564694902259, 0.10793087903181793, -0.38922014574132524, -0.15038749905406518, -0.5226408162362786, -0.14218745290199064, -0.21308402005655114, 0.06018031438762811, 1.921308686143473, 2.2064972101689766, 1.5759721461837235, 0.9069026734135651, 0.33552251726469984, -0.035802859542777316, -0.3793453869198697, -0.4187758305272733, -0.4243310584370478, -0.34404934667215814, -0.301323848959779, -0.2367415126824331, 1.588576488911242, 2.0265004887825433, 1.558763866118104, 1.0494937119157928, 0.5735039710349656, 0.20167916177213285, -0.0646958006599625, -0.23870330175522128, -0.3614840173402365, -0.47518194790752316, -0.5361737110491408, -0.5150682016116384, 0.8306067151977857, 1.0232593287503842, 0.7405219770802316, 0.48091509341468197, 0.26131991425498924, 0.09773505503759358, -0.011281079726210555, -0.0711442898174626, -0.11353383953237414, -0.1720487117775891, -0.23101811356939903, -0.2602490213256186, 0.9312357493155357, 1.1582764576224214, 0.836560237093328, 0.4971847948766222, 0.17028194958595302, -0.08499671118167014, -0.2506128580170208, -0.3223619141017463, -0.30908428649404807, -0.2645034002471899, -0.21475625731514913, -0.1671698790288339, 1.1060218429377222, 1.337176255129248, 0.8667069212673486, 0.4152218052040871, 0.09024037797241842, -0.09064132688055304, -0.17397251736467303, -0.18082407803590198, -0.1392981312669227, -0.10396525457158969, -0.10179665645243338, -0.11993223988823726, 4.772203206214185, 4.317660512859312, 1.359780075256257, -0.042903365658363495, -0.2641926970401683, 0.11000117023997967, -0.3846731045348918, -0.6405123082292836, -0.7650564376476526, -0.5525305107444347, -0.03677014954027041, 0.2061577341801529, 2.699457227709251, 2.9018011746295778, 1.3476700297331896, 0.0967504391222884, -0.6265974531777676, -0.7478962672201773, -0.7546481168220694, -0.6703410211267566, -0.5254742694198614, -0.3672461558814306, -0.1822207002393514, -0.12356988026061734};

		List<Double> res = (DoublesParser.parseFile("Files/dct.txt"));
		
		for (int i = 0; i>res.size(); i++){
			assertEquals(vals[i], res.get(i));
;		}
	}

}