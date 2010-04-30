/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner, Mircea Diaconescu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 **************************************************************************************************************/
package aors.module.sound;

/**
 * MidiMap
 * 
 * @author Andreas Freier (business.af@web.de), Mircea Diaconescu
 * @date April 02, 2009
 * @version $Revision: 1.1 $
 */
public class MIDIMap {

  public enum INSTRUMENTS {
    PIANO_AcousticGrand,

    PIANO_BrightAcoustic,

    PIANO_ElectricGrand,

    PIANO_HonkyTonk,

    PIANO_EPiano1,

    PIANO_EPiano2,

    PIANO_Harpsichord,

    PIANO_Clav,

    CHROMATIC_PERCUSSION_Celesta,

    CHROMATIC_PERCUSSION_Glockenspiel,

    CHROMATIC_PERCUSSION_MusicBox,

    CHROMATIC_PERCUSSION_Vibraphone,

    CHROMATIC_PERCUSSION_Marimba,

    CHROMATIC_PERCUSSION_Xylophone,

    CHROMATIC_PERCUSSION_TubularBells,

    CHROMATIC_PERCUSSION_Dulcimer,

    ORGAN_DrawbarOrgan,

    ORGAN_PercussiveOrgan,

    ORGAN_RockOrgan,

    ORGAN_ChurchOrgan,

    ORGAN_ReedOrgan,

    ORGAN_Accoridan,

    ORGAN_Harmonica,

    ORGAN_TangoAccordian,

    GUITAR_AcousticGuitar_nylon,

    GUITAR_AcousticGuitar_steel,

    GUITAR_ElectricGuitar_jazz,

    GUITAR_ElectricGuitar_clean,

    GUITAR_ElectricGuitar_muted,

    GUITAR_OverdrivenGuitar,

    GUITAR_DistortionGuitar,

    GUITAR_GuitarHarmonics,

    BASS_AcousticBass,

    BASS_ElectricBass_finger,

    BASS_ElectricBass_pick,

    BASS_FretlessBass,

    BASS_SlapBass1,

    BASS_SlapBass2,

    BASS_SynthBass1,

    BASS_SynthBass2,

    STRINGS_Violin,

    STRINGS_Viola,

    STRINGS_Cello,

    STRINGS_Contrabass,

    STRINGS_TremoloStrings,

    STRINGS_PizzicatoStrings,

    STRINGS_OrchestralStrings,

    STRINGS_Timpani,

    ENSEMBLE_StringEnsemble1,

    ENSEMBLE_StringEnsemble2,

    ENSEMBLE_SynthStrings1,

    ENSEMBLE_SynthStrings2,

    ENSEMBLE_ChoirAahs,

    ENSEMBLE_VoiceOohs,

    ENSEMBLE_SynthVoice,

    ENSEMBLE_OrchestraHit,

    BRASS_Trumpet,

    BRASS_Trombone,

    BRASS_Tuba,

    BRASS_MutedTrumpet,

    BRASS_FrenchHorn,

    BRASS_BrassSection,

    BRASS_SynthBrass1,

    BRASS_SynthBrass2,

    REED_SopranoSax,

    REED_AltoSax,

    REED_TenorSax,

    REED_BaritoneSax,

    REED_Oboe,

    REED_EnglishHorn,

    REED_Bassoon,

    REED_Clarinet,

    PIPE_Piccolo,

    PIPE_Flute,

    PIPE_Recorder,

    PIPE_PanFlute,

    PIPE_BlownBottle,

    PIPE_Shakuhachi,

    PIPE_Whistle,

    PIPE_Ocarina,

    SYNTH_LEAD_Lead1,

    SYNTH_LEAD_Lead2,

    SYNTH_LEAD_Lead3,

    SYNTH_LEAD_Lead4,

    SYNTH_LEAD_Lead5,

    SYNTH_LEAD_Lead6,

    SYNTH_LEAD_Lead7,

    SYNTH_LEAD_Lead8,

    SYNTH_PAD_Pad1,

    SYNTH_PAD_Pad2,

    SYNTH_PAD_Pad3,

    SYNTH_PAD_Pad4,

    SYNTH_PAD_Pad5,

    SYNTH_PAD_Pad6,

    SYNTH_PAD_Pad7,

    SYNTH_PAD_Pad8,

    SYNTH_EFFECTS_FX1,

    SYNTH_EFFECTS_FX2,

    SYNTH_EFFECTS_FX3,

    SYNTH_EFFECTS_FX4,

    SYNTH_EFFECTS_FX5,

    SYNTH_EFFECTS_FX6,

    SYNTH_EFFECTS_FX7,

    SYNTH_EFFECTS_FX8,

    ETHNIC_Sitar,

    ETHNIC_Banjo,

    ETHNIC_Shamisen,

    ETHNIC_Koto,

    ETHNIC_Kalimba,

    ETHNIC_Bagpipe,

    ETHNIC_Fiddle,

    ETHNIC_Shanai,

    PERCUSSIVE_TinkleBell,

    PERCUSSIVE_Agogo,

    PERCUSSIVE_SteelDrums,

    PERCUSSIVE_Woodblock,

    PERCUSSIVE_TaikoDrum,

    PERCUSSIVE_MelodicTom,

    PERCUSSIVE_SynthDrum,

    PERCUSSIVE_ReverseCymbal,

    SOUND_EFFECTS_GuitarFretNoise,

    SOUND_EFFECTS_BreathNoise,

    SOUND_EFFECTS_Seashore,

    SOUND_EFFECTS_BirdTweet,

    SOUND_EFFECTS_TelephoneRing,

    SOUND_EFFECTS_Helicopter,

    SOUND_EFFECTS_Applause,

    SOUND_EFFECTS_Gunshot
  }

  public enum NOTES {
    C_2,

    Cis_2,

    D_2,

    Dis_2,

    E_2,

    F_2,

    Fis_2,

    G_2,

    Gis_2,

    A_2,

    Ais_2,

    H_2,

    C_1,

    Cis_1,

    D_1,

    Dis_1,

    E_1,

    F_1,

    Fis_1,

    G_1,

    Gis_1,

    A_1,

    Ais_1,

    H_1,

    C,

    Cis,

    D,

    Dis,

    E,

    F,

    Fis,

    G,

    Gis,

    A,

    Ais,

    H,

    C1,

    Cis1,

    D1,

    Dis1,

    E1,

    F1,

    Fis1,

    G1,

    Gis1,

    A1,

    Ais1,

    H1,

    C2,

    Cis2,

    D2,

    Dis2,

    E2,

    F2,

    Fis2,

    G2,

    Gis2,

    A2,

    Ais2,

    H2,

    C3,

    Cis3,

    D3,

    Dis3,

    E3,

    F3,

    Fis3,

    G3,

    Gis3,

    A3,

    Ais3,

    H3,

    C4,

    Cis4,

    D4,

    Dis4,

    E4,

    F4,

    Fis4,

    G4,

    Gis4,

    A4,

    Ais4,

    H4,

    C5,

    Cis5,

    D5,

    Dis5,

    E5,

    F5,

    Fis5,

    G5,

    Gis5,

    A5,

    Ais5,

    H5,

    C6,

    Cis6,

    D6,

    Dis6,

    E6,

    F6,

    Fis6,

    G6,

    Gis6,

    A6,

    Ais6,

    H6,

    C7,

    Cis7,

    D7,

    Dis7,

    E7,

    F7,

    Fis7,

    G7,

    Gis7,

    A7,

    Ais7,

    H7,

    C8,

    Cis8,

    D8,

    Dis8,

    E8,

    F8,

    Fis8,

    G8
  }

  /**
   * Get the instrument number by name
   * 
   * @param name
   *          the name of the instrument
   * @return the instrument Number
   */
  public int getInstrumentNo(String name) {
    try {
      return INSTRUMENTS.valueOf(name).ordinal();
    } catch (IllegalArgumentException e) {
      return -1;
    }
  }

  /**
   * Get the Note number by name
   * 
   * @param name
   *          the name of the Note
   * @return the Note Number
   */
  public int getNoteNo(String name) {
    try {
      return NOTES.valueOf(name).ordinal();
    } catch (IllegalArgumentException e) {
      return -1;
    }
  }
}
