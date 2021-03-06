/*
 * With this macros/functions, you can specify the profiling keys
 * to aprof for the current exucuted function.
 * 
 * Last changed: $Date: 2012-08-01 12:58:45 +0200 (Wed, 01 Aug 2012) $
 * Revision:     $Rev$
 */

/*
   This file is part of aprof, an input sensitive profiler.

   Copyright (C) 2011-2012, Emilio Coppa (ercoppa@gmail.com),
                            Camil Demetrescu,
                            Irene Finocchi

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
   02111-1307, USA.

   The GNU General Public License is contained in the file COPYING.
*/

#include "../valgrind/include/valgrind.h"
#include "../valgrind/aprof/conf_keys.h"

/* only a custom key */
#define SET_PROFILE_KEY(k) set_profile_keys(CUSTOM, key, 0, 0); 
/* first key RMS, second key custom */
#define SET_PROFILE_RMS_KEY(k) set_profile_keys(RMS_CUSTOM, 0, k, 0);
/* first key custom, second key RMS */
#define SET_PROFILE_KEY_RMS(k) set_profile_keys(CUSTOM_RMS, k, 0, 0);
/* first key custom, second key custom */
#define SET_PROFILE_KEY_KEY(k1, k2) set_profile_keys(CUSTOM_CUSTOM, k1, k2, 0);
/* first key RMS, second key calling context */
#define SET_PROFILE_RMS_CC() set_profile_keys(RMS_CC, 0, 0, 0);
/* first key RMS, second key custom, third key cc */
#define SET_PROFILE_RMS_KEY_CC(k) set_profile_keys(RMS_CUSTOM_CC, 0, k, 0);
/* first key RMS, second key custom, third key custom */
#define SET_PROFILE_RMS_KEY_KEY(k1, k2) set_profile_keys(RMS_CUSTOM_CUSTOM, 0, k1, k2);


void set_profile_keys (unsigned int conf, unsigned long long key1, 
						unsigned long long key2, unsigned long key3) {
	
	#ifdef RUNNING_ON_VALGRIND
	int none;
	VALGRIND_DO_CLIENT_REQUEST(none, 0, VG_USERREQ_TOOL_BASE('V', 'A'), 
											conf, &key1, &key2, &key3, 0);	
	#endif
	
	return;

}
