include $(top_srcdir)/Makefile.tool.am

EXTRA_DIST = docs/va-manual.xml
CFLAGS += -Werror -Winline

noinst_HEADERS = \
	data-common.h \
	hashtable/hashtable.h \
	aprof.h

#----------------------------------------------------------------------------
# aprof-<platform>
#----------------------------------------------------------------------------

noinst_PROGRAMS  = aprof-@VGCONF_ARCH_PRI@-@VGCONF_OS@
if VGCONF_HAVE_PLATFORM_SEC
noinst_PROGRAMS += aprof-@VGCONF_ARCH_SEC@-@VGCONF_OS@
endif

aprof_SOURCES_COMMON =  cct.c fwrite.c thread.c events.c \
						./hashtable/hashtable.c cost.c \
						syscall.c function.c memory.c report.c \
						callstack.c debug.c main.c lookup_table.c \
						overflow.c merge.c util.c

aprof_@VGCONF_ARCH_PRI@_@VGCONF_OS@_SOURCES      = \
	$(aprof_SOURCES_COMMON)
aprof_@VGCONF_ARCH_PRI@_@VGCONF_OS@_CPPFLAGS     = \
	$(AM_CPPFLAGS_@VGCONF_PLATFORM_PRI_CAPS@)
aprof_@VGCONF_ARCH_PRI@_@VGCONF_OS@_CFLAGS       = \
	$(AM_CFLAGS_@VGCONF_PLATFORM_PRI_CAPS@)
aprof_@VGCONF_ARCH_PRI@_@VGCONF_OS@_DEPENDENCIES = \
	$(TOOL_DEPENDENCIES_@VGCONF_PLATFORM_PRI_CAPS@)
aprof_@VGCONF_ARCH_PRI@_@VGCONF_OS@_LDADD        = \
	$(TOOL_LDADD_@VGCONF_PLATFORM_PRI_CAPS@)
aprof_@VGCONF_ARCH_PRI@_@VGCONF_OS@_LDFLAGS      = \
	$(TOOL_LDFLAGS_@VGCONF_PLATFORM_PRI_CAPS@)
aprof_@VGCONF_ARCH_PRI@_@VGCONF_OS@_LINK = \
	$(top_builddir)/coregrind/link_tool_exe_@VGCONF_OS@ \
	@VALT_LOAD_ADDRESS_PRI@ \
	$(LINK) \
	$(aprof_@VGCONF_ARCH_PRI@_@VGCONF_OS@_CFLAGS) \
	$(aprof_@VGCONF_ARCH_PRI@_@VGCONF_OS@_LDFLAGS)

if VGCONF_HAVE_PLATFORM_SEC
aprof_@VGCONF_ARCH_SEC@_@VGCONF_OS@_SOURCES      = \
	$(aprof_SOURCES_COMMON)
aprof_@VGCONF_ARCH_SEC@_@VGCONF_OS@_CPPFLAGS     = \
	$(AM_CPPFLAGS_@VGCONF_PLATFORM_SEC_CAPS@)
aprof_@VGCONF_ARCH_SEC@_@VGCONF_OS@_CFLAGS       = \
	$(AM_CFLAGS_@VGCONF_PLATFORM_SEC_CAPS@)
aprof_@VGCONF_ARCH_SEC@_@VGCONF_OS@_DEPENDENCIES = \
	$(TOOL_DEPENDENCIES_@VGCONF_PLATFORM_SEC_CAPS@)
aprof_@VGCONF_ARCH_SEC@_@VGCONF_OS@_LDADD        = \
	$(TOOL_LDADD_@VGCONF_PLATFORM_SEC_CAPS@)
aprof_@VGCONF_ARCH_SEC@_@VGCONF_OS@_LDFLAGS      = \
	$(TOOL_LDFLAGS_@VGCONF_PLATFORM_SEC_CAPS@)
aprof_@VGCONF_ARCH_SEC@_@VGCONF_OS@_LINK = \
	$(top_builddir)/coregrind/link_tool_exe_@VGCONF_OS@ \
	@VALT_LOAD_ADDRESS_SEC@ \
	$(LINK) \
	$(aprof_@VGCONF_ARCH_SEC@_@VGCONF_OS@_CFLAGS) \
	$(aprof_@VGCONF_ARCH_SEC@_@VGCONF_OS@_LDFLAGS)
endif


