using System;

namespace Cordova.Extension.Commands
{
    public class AdMobOptions
    {
        public string adId { get; set; }
        public bool? autoShow { get; set; }
        
        public bool? isTesting { get; set; }
        public bool? logVerbose { get; set; }
		
        public string adSize { get; set; }
        public int? width { get; set; }
        public int? height { get; set; }
        
        public bool? overlap { get; set; }
        public bool? orientationRenew { get; set; }
		
		public int? position { get; set; }
		public int? x { get; set; }
		public int? y { get; set; }

        public string bannerId { get; set; }
        public string interstitialId { get; set; }
	}

}
