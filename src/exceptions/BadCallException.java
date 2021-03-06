/**
 * Copyright (C) 2016 Desvignes Julian, Louis-Baptiste Trailin, Aymeric Gleye, Rémi Dulong
 */

/**
 This file is part of ARJL.

 ARJL is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 ARJL is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with ARJL.  If not, see <http://www.gnu.org/licenses/>

 */

package exceptions;

/**
 * Exception lancée si l'on appelle une méthode sur un mauvais objet
 * ex : un appareil demande à un lien quel est l'appareil de l'autre côté, alors que lui même n'y est pas connecté
 *
 * Utilisée partout dans le moteur de simulation, afin de simplifier les catch de l'interface
 * @author J. Desvignes
 */

public class BadCallException extends Exception
{

}